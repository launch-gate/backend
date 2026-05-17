package com.launchgate.contest.dto.stage;

import com.launchgate.contest.dto.FieldParticipantResponse;
import com.launchgate.contest.dto.resources.ResourceResponse;
import com.launchgate.contest.enums.ScoreScale;

import java.time.Instant;
import java.util.List;

/**
 * Информацию о стадии конкурса для участника.
 *
 * @param id          идентификатор стадии.
 * @param order       позиция стадии
 * @param title       название стадии
 * @param description описание стадии
 * @param rules       правила проведения стадии
 * @param deadlineAt  дата окончания стадии
 * @param eliminating стадия блокирующая
 * @param scoreScale  шкала оценки
 * @param fields      список полей
 * @param resources   список ресурсов
 */
public record StageParticipantResponse(
        Long id,
        Integer order,
        String title,
        String description,
        String rules,
        Instant deadlineAt,
        Boolean eliminating,
        ScoreScale scoreScale,
        List<FieldParticipantResponse> fields,
        List<ResourceResponse> resources
) {
}
