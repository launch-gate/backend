package com.launchgate.evaluation.dto;

import java.math.BigDecimal;

/**
 * Суммаризированный результат проверки.
 *
 * @param submissionId     идентификатор отправленного решения этапа.
 * @param averageScore     средняя оценка.
 * @param completedReviews завершенные проверки.
 */
public record ReviewSummary(
        Long submissionId,
        BigDecimal averageScore,
        long completedReviews
) {
}
