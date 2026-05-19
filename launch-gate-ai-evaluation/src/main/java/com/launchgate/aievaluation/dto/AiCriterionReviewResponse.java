package com.launchgate.aievaluation.dto;

import com.launchgate.aievaluation.entity.AiCriterionReviewStatus;
import java.math.BigDecimal;
import java.util.List;

/**
 * Ответ с результатами ИИ-оценки по конкретному критерию.
 *
 * @param criterionId уникальный идентификатор критерия оценки
 * @param order       порядковый номер критерия при отображении
 * @param description описание критерия
 * @param status      статус проверки критерия нейросетью
 * @param score       выставленный ИИ балл за соответствие критерию
 * @param verdict     вердикт
 * @param answer      анализируемый ответ участника, который проверялся по этому критерию
 * @param evidence    список улик/доказательств (цитат или фрагментов кода), на основе которых ИИ сделал вывод
 * @param confidence  уровень уверенности ИИ в корректности своей оценки (от 0.0 до 1.0)
 */
public record AiCriterionReviewResponse(
        Long criterionId,
        int order,
        String description,
        AiCriterionReviewStatus status,
        Integer score,
        String verdict,
        String answer,
        List<AiEvidenceResponse> evidence,
        BigDecimal confidence
) {
}
