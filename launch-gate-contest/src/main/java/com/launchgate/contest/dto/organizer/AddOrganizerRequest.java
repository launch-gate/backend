package com.launchgate.contest.dto.organizer;

import com.launchgate.contest.enums.ContestRole;
import jakarta.validation.constraints.NotNull;

/**
 * Запрос на добавление организатора.
 *
 * @param userId идентификатор пользователя
 * @param role   роль пользователя.
 */
public record AddOrganizerRequest(
        @NotNull
        Long userId,
        @NotNull
        ContestRole role
) {
}
