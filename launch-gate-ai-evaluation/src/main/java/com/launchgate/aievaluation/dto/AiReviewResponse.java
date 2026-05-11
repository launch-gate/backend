package com.launchgate.aievaluation.dto;

import com.launchgate.aievaluation.entity.AiReviewStatus;
import java.time.Instant;
import java.util.List;

public record AiReviewResponse(
        Long id,
        Long submissionId,
        AiReviewStatus status,
        Instant createdAt,
        Instant updatedAt,
        List<AiFieldReviewResponse> fields
) {
}
