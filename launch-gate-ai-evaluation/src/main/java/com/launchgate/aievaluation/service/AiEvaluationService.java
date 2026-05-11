package com.launchgate.aievaluation.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.launchgate.aievaluation.dto.AiReviewLookupResponse;
import com.launchgate.aievaluation.dto.AiReviewResponse;
import com.launchgate.aievaluation.entity.AiCriterionReview;
import com.launchgate.aievaluation.entity.AiCriterionReviewStatus;
import com.launchgate.aievaluation.entity.AiFieldReview;
import com.launchgate.aievaluation.entity.AiFieldReviewStatus;
import com.launchgate.aievaluation.entity.AiReviewSourceType;
import com.launchgate.aievaluation.entity.AiSubmissionReview;
import com.launchgate.aievaluation.repository.AiSubmissionReviewRepository;
import com.launchgate.aievaluation.service.client.AiProviderReviewResult;
import com.launchgate.aievaluation.service.client.AiReviewHttpClient;
import com.launchgate.aievaluation.service.model.PreparedFieldPayload;
import com.launchgate.aievaluation.service.strategy.AiFieldPayloadStrategy;
import com.launchgate.contest.entity.FieldCriterion;
import com.launchgate.contest.entity.SubmissionField;
import com.launchgate.contest.service.ContestReaderService;
import com.launchgate.identity.dto.AuthenticatedUser;
import com.launchgate.identity.service.UserService;
import com.launchgate.submission.entity.SubmissionValue;
import com.launchgate.submission.service.SubmissionReaderService;
import java.time.Clock;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AiEvaluationService {
    private final AiSubmissionReviewRepository aiSubmissionReviewRepository;
    private final SubmissionReaderService submissionReaderService;
    private final ContestReaderService contestReaderService;
    private final AiEvaluationAccessService aiEvaluationAccessService;
    private final List<AiFieldPayloadStrategy> payloadStrategies;
    private final AiReviewHttpClient aiReviewHttpClient;
    private final AiReviewMapper aiReviewMapper;
    private final ObjectMapper objectMapper;
    private final UserService userService;
    private final Clock clock;

    @Transactional(readOnly = true)
    public AiReviewLookupResponse getReview(AuthenticatedUser user, Long submissionId) {
        var submission = submissionReaderService.getSubmissionById(submissionId);
        var stage = contestReaderService.getStageById(submission.getStageId());
        aiEvaluationAccessService.requireReviewAccess(user, stage);

        return aiSubmissionReviewRepository.findBySubmission_Id(submissionId)
                .map(review -> new AiReviewLookupResponse(true, aiReviewMapper.toResponse(review)))
                .orElseGet(() -> new AiReviewLookupResponse(false, null));
    }

    @Transactional
    public AiReviewResponse runReview(AuthenticatedUser user, Long submissionId) {
        var submission = submissionReaderService.getSubmissionById(submissionId);
        var stage = contestReaderService.getStageById(submission.getStageId());
        aiEvaluationAccessService.requireReviewAccess(user, stage);

        var submissionValues = submissionReaderService.getSubmissionValues(submissionId).stream()
                .collect(Collectors.toMap(value -> value.getField().getId(), Function.identity(), (left, _) -> left));
        var fields = contestReaderService.getSubmissionFields(stage.getId()).stream()
                .sorted(Comparator.comparing(SubmissionField::getOrder))
                .toList();

        var requestedByUser = userService.getUserById(user.id());
        var review = aiSubmissionReviewRepository.findBySubmission_Id(submissionId)
                .orElseGet(() -> new AiSubmissionReview(submission, requestedByUser, Instant.now(clock)));

        var fieldReviews = fields.stream()
                .map(field -> reviewField(review, field, submissionValues.get(field.getId())))
                .toList();
        review.refresh(requestedByUser, fieldReviews, Instant.now(clock));
        return aiReviewMapper.toResponse(aiSubmissionReviewRepository.save(review));
    }

    private AiFieldReview reviewField(AiSubmissionReview review, SubmissionField field, SubmissionValue submissionValue) {
        var criteria = orderedCriteria(field);
        if (criteria.isEmpty()) {
            var fieldReview = new AiFieldReview(
                    review,
                    field,
                    submissionValue,
                    field.getOrder(),
                    field.getTitle(),
                    field.getType().name(),
                    AiFieldReviewStatus.SKIPPED_NO_CRITERIA,
                    AiReviewSourceType.UNSUPPORTED,
                    "This field has no configured AI review criteria"
            );
            fieldReview.replaceCriteria(List.of());
            return fieldReview;
        }

        PreparedFieldPayload prepared;
        try {
            prepared = payloadStrategies.stream()
                    .filter(strategy -> strategy.supports(field, submissionValue))
                    .findFirst()
                    .orElseThrow()
                    .prepare(field, submissionValue);
        } catch (Exception exception) {
            return createFieldReview(
                    review,
                    new PreparedFieldPayload(
                            field,
                            field.getOrder(),
                            field.getTitle(),
                            field.getType(),
                            submissionValue,
                            AiFieldReviewStatus.FAILED,
                            AiReviewSourceType.UNSUPPORTED,
                            "Could not prepare submission data for AI review",
                            null,
                            null,
                            criteria
                    ),
                    createFailedCriterionReviews(criteria, "Could not prepare submission data for AI review")
            );
        }

        if (!prepared.readyForRemoteReview()) {
            return createFieldReview(review, prepared, createLocalCriterionReviews(prepared, criteria));
        }

        try {
            var providerResults = switch (prepared.payloadKind()) {
                case TEXT -> aiReviewHttpClient.reviewText(prepared.fieldTitle(), prepared.payloadValue(), criteria);
                case REPOSITORY -> aiReviewHttpClient.reviewRepository(prepared.fieldTitle(), prepared.payloadValue(), criteria);
            };
            return createFieldReview(review, prepared, createCompletedCriterionReviews(criteria, providerResults));
        } catch (Exception exception) {
            return createFieldReview(
                    review,
                    new PreparedFieldPayload(
                            prepared.field(),
                            prepared.fieldOrder(),
                            prepared.fieldTitle(),
                            prepared.fieldType(),
                            prepared.submissionValue(),
                            AiFieldReviewStatus.FAILED,
                            prepared.sourceType(),
                            "AI review service is temporarily unavailable",
                            prepared.payloadKind(),
                            prepared.payloadValue(),
                            prepared.criteria()
                    ),
                    createFailedCriterionReviews(criteria, "AI review service is temporarily unavailable")
            );
        }
    }

    private AiFieldReview createFieldReview(
            AiSubmissionReview review,
            PreparedFieldPayload prepared,
            List<AiCriterionReview> criterionReviews
    ) {
        var fieldReview = new AiFieldReview(
                review,
                prepared.field(),
                prepared.submissionValue(),
                prepared.fieldOrder(),
                prepared.fieldTitle(),
                prepared.fieldType().name(),
                resolveStatus(criterionReviews, prepared),
                prepared.sourceType(),
                prepared.message()
        );
        fieldReview.replaceCriteria(criterionReviews.stream()
                .map(criterionReview -> new AiCriterionReview(
                        fieldReview,
                        criterionReview.getCriterion(),
                        criterionReview.getOrder(),
                        criterionReview.getDescription(),
                        criterionReview.getStatus(),
                        criterionReview.getScore(),
                        criterionReview.getVerdict(),
                        criterionReview.getAnswer(),
                        criterionReview.getEvidenceJson(),
                        criterionReview.getConfidence()
                ))
                .toList());
        return fieldReview;
    }

    private List<AiCriterionReview> createLocalCriterionReviews(PreparedFieldPayload prepared, List<FieldCriterion> criteria) {
        var status = switch (prepared.status()) {
            case SKIPPED_NO_DATA, SKIPPED_NO_CRITERIA -> AiCriterionReviewStatus.SKIPPED;
            case UNSUPPORTED_FORMAT -> AiCriterionReviewStatus.UNSUPPORTED_FORMAT;
            default -> AiCriterionReviewStatus.FAILED;
        };
        return criteria.stream()
                .map(criterion -> new AiCriterionReview(
                        null,
                        criterion,
                        criterion.getOrder(),
                        criterion.getDescription(),
                        status,
                        null,
                        null,
                        prepared.message(),
                        null,
                        null
                ))
                .toList();
    }

    private List<AiCriterionReview> createFailedCriterionReviews(List<FieldCriterion> criteria, String message) {
        return criteria.stream()
                .map(criterion -> new AiCriterionReview(
                        null,
                        criterion,
                        criterion.getOrder(),
                        criterion.getDescription(),
                        AiCriterionReviewStatus.FAILED,
                        null,
                        null,
                        message,
                        null,
                        null
                ))
                .toList();
    }

    private List<AiCriterionReview> createCompletedCriterionReviews(
            List<FieldCriterion> criteria,
            List<AiProviderReviewResult> providerResults
    ) {
        var resultsByCriterionId = providerResults.stream()
                .collect(Collectors.toMap(AiProviderReviewResult::criterionId, Function.identity(), (left, right) -> left));
        return criteria.stream()
                .map(criterion -> {
                    var result = resultsByCriterionId.get(criterion.getId().toString());
                    if (result == null) {
                        return new AiCriterionReview(
                                null,
                                criterion,
                                criterion.getOrder(),
                                criterion.getDescription(),
                                AiCriterionReviewStatus.FAILED,
                                null,
                                null,
                                "AI service did not return a result for this criterion",
                                null,
                                null
                        );
                    }
                    return new AiCriterionReview(
                            null,
                            criterion,
                            criterion.getOrder(),
                            criterion.getDescription(),
                            AiCriterionReviewStatus.COMPLETED,
                            result.score(),
                            result.verdict(),
                            result.answer(),
                            evidenceJson(result),
                            result.confidence()
                    );
                })
                .toList();
    }

    private String evidenceJson(AiProviderReviewResult result) {
        try {
            return objectMapper.writeValueAsString(result.evidence() == null ? List.of() : result.evidence());
        } catch (Exception exception) {
            return "[]";
        }
    }

    private AiFieldReviewStatus resolveStatus(List<AiCriterionReview> criterionReviews, PreparedFieldPayload prepared) {
        if (prepared.status() != null) {
            return prepared.status();
        }
        return criterionReviews.stream().allMatch(review -> review.getStatus() == AiCriterionReviewStatus.COMPLETED)
                ? AiFieldReviewStatus.COMPLETED
                : AiFieldReviewStatus.FAILED;
    }

    private List<FieldCriterion> orderedCriteria(SubmissionField field) {
        return field.getCriteria().stream()
                .sorted(Comparator.comparing(FieldCriterion::getOrder))
                .toList();
    }
}
