package com.launchgate.contest.dto.team;

import java.util.List;

/**
 * Информация о всех командах.
 *
 * @param teams список команд
 */
public record AllTeamsResponse(
        List<TeamResponse> teams
) {
}
