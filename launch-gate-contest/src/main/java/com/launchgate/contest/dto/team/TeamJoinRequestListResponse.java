package com.launchgate.contest.dto.team;

import java.util.List;

/**
 * Ответ со списком запросов на вступление в команду.
 *
 * @param requests списком запросов на вступление в команду
 */
public record TeamJoinRequestListResponse(
        List<TeamJoinRequestResponse> requests
) {
}
