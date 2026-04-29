package com.launchgate.evaluation.dto;

import com.launchgate.evaluation.entity.ReviewStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.Instant;

@Schema(description = "Expert review response")
public record ReviewResponse(
        @Schema(description = "Review assignment id", example = "44")
        Long assignmentId,
        @Schema(description = "Submitted stage solution id", example = "15")
        Long submissionId,
        @Schema(description = "Expert user id", example = "8")
        Long expertId,
        @Schema(description = "Current review status")
        ReviewStatus status,
        @Schema(description = "Saved score", example = "86.50")
        BigDecimal score,
        @Schema(description = "Saved review comment")
        String comment,
        @Schema(description = "Review finalization timestamp")
        Instant finalizedAt
) {
}
