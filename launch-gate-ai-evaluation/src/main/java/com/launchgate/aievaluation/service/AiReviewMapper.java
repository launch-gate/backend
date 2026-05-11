package com.launchgate.aievaluation.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.launchgate.aievaluation.dto.AiCriterionReviewResponse;
import com.launchgate.aievaluation.dto.AiEvidenceResponse;
import com.launchgate.aievaluation.dto.AiFieldReviewResponse;
import com.launchgate.aievaluation.dto.AiReviewResponse;
import com.launchgate.aievaluation.entity.AiCriterionReview;
import com.launchgate.aievaluation.entity.AiFieldReview;
import com.launchgate.aievaluation.entity.AiSubmissionReview;
import com.launchgate.contest.entity.FieldType;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AiReviewMapper {
    private static final TypeReference<List<AiEvidenceResponse>> EVIDENCE_TYPE = new TypeReference<>() {
    };

    private final ObjectMapper objectMapper;

    public AiReviewResponse toResponse(AiSubmissionReview review) {
        var fieldReviews = review.getFieldReviews().stream()
                .sorted(Comparator.comparing(AiFieldReview::getOrder))
                .map(this::toFieldResponse)
                .toList();
        return new AiReviewResponse(
                review.getId(),
                review.getSubmission().getId(),
                review.getStatus(),
                review.getCreatedAt(),
                review.getUpdatedAt(),
                fieldReviews
        );
    }

    private AiFieldReviewResponse toFieldResponse(AiFieldReview fieldReview) {
        var criteria = fieldReview.getCriteria().stream()
                .sorted(Comparator.comparing(AiCriterionReview::getOrder))
                .map(this::toCriterionResponse)
                .toList();
        return new AiFieldReviewResponse(
                fieldReview.getField().getId(),
                fieldReview.getOrder(),
                fieldReview.getTitle(),
                FieldType.valueOf(fieldReview.getType()),
                fieldReview.getStatus(),
                fieldReview.getSourceType(),
                fieldReview.getMessage(),
                criteria
        );
    }

    private AiCriterionReviewResponse toCriterionResponse(AiCriterionReview criterionReview) {
        return new AiCriterionReviewResponse(
                criterionReview.getCriterion().getId(),
                criterionReview.getOrder(),
                criterionReview.getDescription(),
                criterionReview.getStatus(),
                criterionReview.getScore(),
                criterionReview.getVerdict(),
                criterionReview.getAnswer(),
                evidence(criterionReview),
                criterionReview.getConfidence()
        );
    }

    private List<AiEvidenceResponse> evidence(AiCriterionReview criterionReview) {
        if (criterionReview.getEvidenceJson() == null || criterionReview.getEvidenceJson().isBlank()) {
            return List.of();
        }
        try {
            return objectMapper.readValue(criterionReview.getEvidenceJson(), EVIDENCE_TYPE);
        } catch (Exception exception) {
            return List.of();
        }
    }
}
