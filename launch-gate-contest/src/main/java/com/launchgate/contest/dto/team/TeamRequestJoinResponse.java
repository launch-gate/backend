package com.launchgate.contest.dto.team;

/**
 * Ответ на запрос о вступлении в команду
 *
 * @param joinRequestId идентификатор запроса на вступление в команду.
 */
public record TeamRequestJoinResponse(
        Long joinRequestId
) {
}
