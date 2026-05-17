package com.launchgate.contest.dto.team;

import com.launchgate.contest.enums.TeamJoinRequestStatus;
import java.time.Instant;

public record TeamJoinRequestResponse(
        Long id,
        Long teamId,
        Long participantId,
        String participantFullName,
        String participantNickname,
        TeamJoinRequestStatus status,
        Instant createdAt
) {
}
