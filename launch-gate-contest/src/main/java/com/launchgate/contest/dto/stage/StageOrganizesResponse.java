package com.launchgate.contest.dto.stage;

import com.launchgate.contest.dto.FieldResponse;
import com.launchgate.contest.dto.resources.ResourceResponse;
import com.launchgate.contest.enums.ScoreScale;

import java.time.Instant;
import java.util.List;

/**
 * Информация о стадии конкурса для организатора.
 *
 * @param id          идентификатор стадии
 * @param order       позиция стадии
 * @param title       название стадии
 * @param description описание стадии
 * @param rules       правила стадии
 * @param extraInfo   допольнительная информация
 * @param deadlineAt  дата окончания стадии
 * @param eliminating стадия блокирующая.
 * @param scoreScale  шкала оценки
 * @param fields      поля
 * @param resources   ресурсы
 */
public record StageOrganizesResponse(
        Long id,
        int order,
        String title,
        String description,
        String rules,
        String extraInfo,
        Instant deadlineAt,
        boolean eliminating,
        ScoreScale scoreScale,
        List<FieldResponse> fields,
        List<ResourceResponse> resources
) {
}
