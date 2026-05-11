package com.launchgate.aievaluation.dto;

public record AiReviewLookupResponse(
        boolean exists,
        AiReviewResponse review
) {
}
