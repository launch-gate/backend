package com.launchgate.export.dto;

import java.math.BigDecimal;

/**
 * Модель строки в конкурсном рейтинге.
 *
 * @param stage стадия
 * @param project проект
 * @param submissionId идентификатор поданной работы
 * @param score оценка
 * @param completedReviews ревью
 */
public record RankingRow(
        String stage,
        String project,
        Long submissionId,
        BigDecimal score,
        Long completedReviews
) {
}
