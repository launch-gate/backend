package com.launchgate.contest.dto.team;

import java.util.List;

/**
 * Информация о команде.
 *
 * @param id          идентификатор команды.
 * @param contestId   идентификатор кокурса.
 * @param leaderId    идентификатор лидера команды.
 * @param name        название команды.
 * @param inviteToken токен для приглашения в команду.
 * @param memberIds   список идентификаторов участников команды.
 */
public record TeamResponse(
        Long id,
        Long contestId,
        Long leaderId,
        String name,
        String inviteToken,
        List<Long> memberIds
) {
}
