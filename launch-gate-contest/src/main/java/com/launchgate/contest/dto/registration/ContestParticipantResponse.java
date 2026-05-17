package com.launchgate.contest.dto.registration;

import java.time.Instant;

/**
 * Информация об участнике конкурса.
 * @param userId идентификатор участника.
 * @param fullName польное имя участника.
 * @param nickname nickname участника.
 * @param bio информация об участнике.
 * @param registeredAt дата регистрации.
 */
public record ContestParticipantResponse(
        Long userId,
        String fullName,
        String nickname,
        String bio,
        Instant registeredAt
) {
}
