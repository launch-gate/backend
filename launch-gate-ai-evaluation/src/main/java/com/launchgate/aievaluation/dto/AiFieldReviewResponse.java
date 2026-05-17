package com.launchgate.aievaluation.dto;

import com.launchgate.aievaluation.entity.AiFieldReviewStatus;
import com.launchgate.aievaluation.entity.AiReviewSourceType;
import com.launchgate.contest.enums.FieldType;
import java.util.List;

public record AiFieldReviewResponse(
        Long fieldId,
        int order,
        String title,
        FieldType type,
        AiFieldReviewStatus status,
        AiReviewSourceType sourceType,
        String message,
        List<AiCriterionReviewResponse> criteria
) {
}
