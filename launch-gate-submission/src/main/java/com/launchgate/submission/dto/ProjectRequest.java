package com.launchgate.submission.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Project workspace creation payload")
public record ProjectRequest(
        @Schema(description = "Contest id", example = "12")
        @NotNull Long contestId,
        @Schema(description = "Team id for team contest; null for individual contest", example = "5")
        Long teamId
) {
}
