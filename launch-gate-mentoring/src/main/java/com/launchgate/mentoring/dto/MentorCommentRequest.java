package com.launchgate.mentoring.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Комментарий ментора к отправленному решению этапа.
 *
 * @param stageSubmissionId идентификатор отправленного решения этапа.
 * @param text              комментарий.
 */
@Schema(description = "Комментарий ментора к отправленному решению этапа")
public record MentorCommentRequest(
        @Schema(description = "Идентификатор отправленного решения этапа", example = "19")
        @NotNull Long stageSubmissionId,
        @Schema(description = "Комментарий", example = "Комментарий")
        @NotBlank String text
) {
}
