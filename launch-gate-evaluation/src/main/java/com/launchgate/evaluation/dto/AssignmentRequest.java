package com.launchgate.evaluation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Expert assignment request")
public record AssignmentRequest(
        @Schema(description = "Submitted stage solution id", example = "15")
        @NotNull Long submissionId,
        @Schema(description = "Expert user id", example = "7")
        @NotNull Long expertUserId
) {
}
