package com.launchgate.contest.dto.registration;

import java.time.Instant;

public record ContestParticipantResponse(
        Long userId,
        String fullName,
        String nickname,
        String bio,
        Instant registeredAt
) {
}
