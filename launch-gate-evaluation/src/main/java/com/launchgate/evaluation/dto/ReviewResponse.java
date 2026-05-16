package com.launchgate.evaluation.dto;

import com.launchgate.evaluation.entity.ReviewStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.Instant;

/**
 * Результат экспертной проверки.
 * @param assignmentId идентификатор назначения на проверку.
 * @param submissionId идентификатор отправленного решения этапа.
 * @param expertId идентификатор эксперта.
 * @param status текущий статус проверки.
 * @param score оценка.
 * @param comment комментарии по проверке.
 * @param finalizedAt дата и время завершения проверки.
 */
@Schema(description = "Данные экспертной проверки")
public record ReviewResponse(
        @Schema(description = "Идентификатор назначения на проверку", example = "44")
        Long assignmentId,
        @Schema(description = "Идентификатор отправленного решения этапа", example = "15")
        Long submissionId,
        @Schema(description = "Идентификатор эксперта", example = "8")
        Long expertId,
        @Schema(description = "Текущий статус проверки")
        ReviewStatus status,
        @Schema(description = "Оценка", example = "86.50")
        BigDecimal score,
        @Schema(description = "Комментарии по проверке")
        String comment,
        @Schema(description = "Дата и время завершения проверки")
        Instant finalizedAt
) {
}
