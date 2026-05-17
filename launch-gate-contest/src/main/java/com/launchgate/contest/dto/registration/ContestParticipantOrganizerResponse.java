package com.launchgate.contest.dto.registration;

import java.time.Instant;

/**
 * Информация об организаторе конкурса.
 *
 * @param userId       идентификатор организатора.
 * @param email        почта организатора.
 * @param fullName     полное имя организатора.
 * @param nickname     nickname организатора.
 * @param bio          информация об организаторе.
 * @param registeredAt дата и время регистрации.
 */
public record ContestParticipantOrganizerResponse(
        Long userId,
        String email,
        String fullName,
        String nickname,
        String bio,
        Instant registeredAt
) {
}
