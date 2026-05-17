package com.launchgate.evaluation.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

/**
 * Данные черновика проверки.
 *
 * @param score   оценка.
 * @param comment комментарий.
 */
@Schema(description = "Данные черновика проверки")
public record ReviewDraftRequest(
        @Schema(description = "Оценка за критерий этапа", example = "86.50")
        BigDecimal score,
        @Schema(description = "Комментарий эксперта к отправленному решению",
                example = "Сильное решение, но разделу с метриками не хватает доказательной базы.")
        String comment
) {
}
