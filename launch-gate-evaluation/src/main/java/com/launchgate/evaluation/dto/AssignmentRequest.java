package com.launchgate.evaluation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

/**
 * Запрос на назначение эксперта.
 * @param submissionId
 * @param expertUserId
 */
@Schema(description = "Запрос на назначение эксперта")
public record AssignmentRequest(
        @Schema(description = "Идентификатор этапа", example = "15")
        @NotNull Long submissionId,
        @Schema(description = "Идентификатор эксперта", example = "7")
        @NotNull Long expertUserId
) {
}
