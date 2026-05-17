package com.launchgate.mentoring.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

/**
 * Данные комментария ментора.
 *
 * @param id                идентификатор комментария.
 * @param stageSubmissionId идентификатор отправленного решения этапа.
 * @param mentorId          идентификатор ментора.
 * @param text              комментарий.
 * @param createdAt         дата создания комментария.
 */
@Schema(description = "Ответ с данными комментария ментора")
public record MentorCommentResponse(
        @Schema(description = "Идентификатор комментария", example = "71")
        Long id,
        @Schema(description = "Идентификатор отправленного решения этапа.", example = "19")
        Long stageSubmissionId,
        @Schema(description = "Идентификатор ментора", example = "8")
        Long mentorId,
        @Schema(description = "Комментарий")
        String text,
        @Schema(description = "Дата создания комментария")
        Instant createdAt
) {
}
