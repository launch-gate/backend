package com.launchgate.contest.dto.stage;

import java.util.List;

/**
 * Информация об стадиях конкурса для участника.
 *
 * @param stages список стадий конкурса.
 */
public record StageParticipantListResponse(
        List<StageParticipantResponse> stages
) {
}
