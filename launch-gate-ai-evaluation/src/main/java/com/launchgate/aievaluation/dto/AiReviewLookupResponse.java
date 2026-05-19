package com.launchgate.aievaluation.dto;

/**
 * Ответ на запрос поиска результатов ИИ-оценивания.
 *
 * @param exists флаг, указывающий, существуют ли сохраненные результаты проверки ИИ для данного решения
 * @param review результат ИИ-проверки
 */
public record AiReviewLookupResponse(
        boolean exists,
        AiReviewResponse review
) {
}
