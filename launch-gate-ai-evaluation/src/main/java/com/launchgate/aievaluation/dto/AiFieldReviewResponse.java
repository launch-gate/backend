package com.launchgate.aievaluation.dto;

import com.launchgate.aievaluation.entity.AiFieldReviewStatus;
import com.launchgate.aievaluation.entity.AiReviewSourceType;
import com.launchgate.contest.enums.FieldType;
import java.util.List;

/**
 * Ответ с результатами ИИ-проверки для конкретного поля формы.
 * Содержит общий статус анализа поля и детализированную оценку по каждому привязанному критерию.
 *
 * @param fieldId    уникальный идентификатор проверяемого поля
 * @param order      порядковый номер поля на форме
 * @param title      название поля
 * @param type       тип поля
 * @param status     общий статус проверки данного поля нейросетью
 * @param sourceType источник данных, который анализировал ИИ
 * @param message    информационное сообщение или текст ошибки, если проверка не удалась
 * @param criteria   список результатов оценки по отдельным критериям этого поля
 */
public record AiFieldReviewResponse(
        Long fieldId,
        int order,
        String title,
        FieldType type,
        AiFieldReviewStatus status,
        AiReviewSourceType sourceType,
        String message,
        List<AiCriterionReviewResponse> criteria
) {
}
