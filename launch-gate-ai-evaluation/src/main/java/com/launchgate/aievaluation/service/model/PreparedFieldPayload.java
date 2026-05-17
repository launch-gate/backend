package com.launchgate.aievaluation.service.model;

import com.launchgate.aievaluation.entity.AiFieldReviewStatus;
import com.launchgate.aievaluation.entity.AiReviewSourceType;
import com.launchgate.contest.entity.FieldCriterion;
import com.launchgate.contest.enums.FieldType;
import com.launchgate.contest.entity.SubmissionField;
import com.launchgate.submission.entity.SubmissionValue;
import java.util.List;

public record PreparedFieldPayload(
        SubmissionField field,
        int fieldOrder,
        String fieldTitle,
        FieldType fieldType,
        SubmissionValue submissionValue,
        AiFieldReviewStatus status,
        AiReviewSourceType sourceType,
        String message,
        AiPayloadKind payloadKind,
        String payloadValue,
        List<FieldCriterion> criteria
) {
    public boolean readyForRemoteReview() {
        return status == null && payloadKind != null && payloadValue != null && !payloadValue.isBlank();
    }
}
