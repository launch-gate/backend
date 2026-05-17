package com.launchgate.contest.dto.stage;

import com.launchgate.contest.enums.ScoreScale;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;

/**
 * Запрос на создание стадии.
 *
 * @param title       название стадии.
 * @param description описание стадии.
 * @param rules       правила проведения стадии.
 * @param extraInfo   дополнительная информация.
 * @param deadlineAt  дата окончания стадии
 * @param eliminating стадия блокирующая
 * @param scoreScale  шкала оценки
 * @param order       позиция стадии
 */
@Schema(description = "Запрос на создание стадии")
public record StageRequest(
        @Schema(description = "Название стадии")
        @NotBlank
        String title,
        @Schema(description = "Описание стадии")
        String description,
        @Schema(description = "Правила проведения стадии")
        String rules,
        @Schema(description = "Дополнительная информация")
        String extraInfo,
        @Schema(description = "Дата окончания стадии", example = "2026-05-10T18:00:00Z")
        Instant deadlineAt,
        @Schema(description = "Стадия блокирующая")
        Boolean eliminating,
        @Schema(description = "Шкала оценки", example = "POINTS_100")
        @NotNull ScoreScale scoreScale,
        @Schema(description = "Позиция стадии в конкурсе", example = "2")
        Integer order
) {
}
