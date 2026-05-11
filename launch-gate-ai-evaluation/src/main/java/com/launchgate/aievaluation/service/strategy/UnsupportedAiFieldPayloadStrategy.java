package com.launchgate.aievaluation.service.strategy;

import com.launchgate.aievaluation.entity.AiFieldReviewStatus;
import com.launchgate.aievaluation.entity.AiReviewSourceType;
import com.launchgate.aievaluation.service.model.PreparedFieldPayload;
import com.launchgate.contest.entity.FieldCriterion;
import com.launchgate.contest.entity.SubmissionField;
import com.launchgate.submission.entity.SubmissionValue;
import java.util.Comparator;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class UnsupportedAiFieldPayloadStrategy implements AiFieldPayloadStrategy {
    @Override
    public boolean supports(SubmissionField field, SubmissionValue value) {
        return true;
    }

    @Override
    public PreparedFieldPayload prepare(SubmissionField field, SubmissionValue value) {
        return new PreparedFieldPayload(
                field,
                field.getOrder(),
                field.getTitle(),
                field.getType(),
                value,
                AiFieldReviewStatus.UNSUPPORTED_FORMAT,
                AiReviewSourceType.UNSUPPORTED,
                "AI review is currently available only for direct text, text documents and GitHub repositories",
                null,
                null,
                orderedCriteria(field)
        );
    }

    private List<FieldCriterion> orderedCriteria(SubmissionField field) {
        return field.getCriteria().stream()
                .sorted(Comparator.comparing(FieldCriterion::getOrder))
                .toList();
    }
}
