package com.launchgate.contest.dto.registration;

/**
 * Информация о регистрации участника в конкурсе.
 *
 * @param registrationId идентификатор регистрации.
 */
public record ParticipantContestRegistrationResponse(
        Long registrationId
) {
}
