package com.launchgate.aievaluation.service.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.util.List;

public record AiProviderReviewResult(
        @JsonProperty("criterion_id")
        String criterionId,
        @JsonProperty("criterion_description")
        String criterionDescription,
        @JsonProperty("score")
        Integer score,
        @JsonProperty("verdict")
        String verdict,
        @JsonProperty("answer")
        String answer,
        @JsonProperty("evidence")
        List<AiProviderEvidence> evidence,
        @JsonProperty("confidence")
        BigDecimal confidence
) {
}
