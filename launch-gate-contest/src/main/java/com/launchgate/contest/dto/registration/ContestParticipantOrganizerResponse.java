package com.launchgate.contest.dto.registration;

import java.time.Instant;

public record ContestParticipantOrganizerResponse(
        Long userId,
        String email,
        String fullName,
        String nickname,
        String bio,
        Instant registeredAt
) {
}
