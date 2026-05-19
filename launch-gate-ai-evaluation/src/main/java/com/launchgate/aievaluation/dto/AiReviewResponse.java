package com.launchgate.aievaluation.dto;

import com.launchgate.aievaluation.entity.AiReviewStatus;
import java.time.Instant;
import java.util.List;

/**
 * Ответ с полными результатами проверки решения с помощью ИИ.
 *
 * @param id           уникальный идентификатор записи ИИ-обзора в базе данных
 * @param submissionId идентификатор проверяемого решения участника
 * @param status       общий статус процесса ИИ-проверки всего решения
 * @param createdAt    дата и время запуска ИИ-проверки
 * @param updatedAt    дата и время последнего обновления статуса или результатов проверки
 * @param fields       список результатов проверки ИИ по каждому отдельному полю формы
 */
public record AiReviewResponse(
        Long id,
        Long submissionId,
        AiReviewStatus status,
        Instant createdAt,
        Instant updatedAt,
        List<AiFieldReviewResponse> fields
) {
}
