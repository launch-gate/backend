package com.launchgate.contest.dto.stage;

import java.util.List;

/**
 * Информация о настроенных этапов конкурса для организаторов.
 *
 * @param stages список этапов конкурса
 */
public record StageOrganizesListResponse(
        List<StageOrganizesResponse> stages
) {
}
