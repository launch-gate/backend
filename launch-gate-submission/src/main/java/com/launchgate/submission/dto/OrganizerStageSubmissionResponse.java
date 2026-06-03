package com.launchgate.submission.dto;

import com.launchgate.submission.entity.SubmissionStatus;
import java.util.List;

/**
 * Подробная информация об отправленном решении этапа для пространства организатора.
 *
 * @param summary краткая сводка по отправленному решению
 * @param id идентификатор отправленной формы этапа
 * @param status статус отправленной формы этапа
 * @param values заполненные данные формы этапа
 */
public record OrganizerStageSubmissionResponse(
        SubmissionSummary summary,
        Long id,
        SubmissionStatus status,
        List<ValueResponse> values
) {
}
