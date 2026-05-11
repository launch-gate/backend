package com.launchgate.aievaluation.service.strategy;

import com.launchgate.aievaluation.entity.AiFieldReviewStatus;
import com.launchgate.aievaluation.entity.AiReviewSourceType;
import com.launchgate.aievaluation.service.GithubRepositoryVerifier;
import com.launchgate.aievaluation.service.model.AiPayloadKind;
import com.launchgate.aievaluation.service.model.PreparedFieldPayload;
import com.launchgate.contest.entity.FieldCriterion;
import com.launchgate.contest.entity.FieldType;
import com.launchgate.contest.entity.SubmissionField;
import com.launchgate.submission.entity.SubmissionValue;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GithubRepositoryAiFieldPayloadStrategy implements AiFieldPayloadStrategy {
    private final GithubRepositoryVerifier githubRepositoryVerifier;

    @Override
    public boolean supports(SubmissionField field, SubmissionValue value) {
        return field.getType() == FieldType.GITHUB_REPOSITORY
                && value != null
                && value.getValueText() != null
                && githubRepositoryVerifier.isGithubRepositoryUrl(value.getValueText());
    }

    @Override
    public PreparedFieldPayload prepare(SubmissionField field, SubmissionValue value) {
        if (!githubRepositoryVerifier.exists(value.getValueText())) {
            return new PreparedFieldPayload(
                    field,
                    field.getOrder(),
                    field.getTitle(),
                    field.getType(),
                    value,
                    AiFieldReviewStatus.FAILED,
                    AiReviewSourceType.GITHUB_REPOSITORY,
                    "GitHub repository not found or is not publicly accessible",
                    null,
                    null,
                    orderedCriteria(field)
            );
        }
        return new PreparedFieldPayload(
                field,
                field.getOrder(),
                field.getTitle(),
                field.getType(),
                value,
                null,
                AiReviewSourceType.GITHUB_REPOSITORY,
                null,
                AiPayloadKind.REPOSITORY,
                value.getValueText().trim(),
                orderedCriteria(field)
        );
    }

    private List<FieldCriterion> orderedCriteria(SubmissionField field) {
        return field.getCriteria().stream()
                .sorted(Comparator.comparing(FieldCriterion::getOrder))
                .toList();
    }
}
