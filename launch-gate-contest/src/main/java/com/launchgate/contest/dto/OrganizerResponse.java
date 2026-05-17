package com.launchgate.contest.dto;

import com.launchgate.contest.enums.ContestRole;

/**
 * Информация об организаторе.
 *
 * @param id     идентификатор организатора
 * @param userId идентификатор пользователя
 * @param role   роль
 */
public record OrganizerResponse(
        Long id,
        Long userId,
        ContestRole role
) {
}
