package com.launchgate.aievaluation.dto;

import com.launchgate.aievaluation.entity.AiCriterionReviewStatus;
import java.math.BigDecimal;
import java.util.List;

public record AiCriterionReviewResponse(
        Long criterionId,
        int order,
        String description,
        AiCriterionReviewStatus status,
        Integer score,
        String verdict,
        String answer,
        List<AiEvidenceResponse> evidence,
        BigDecimal confidence
) {
}
