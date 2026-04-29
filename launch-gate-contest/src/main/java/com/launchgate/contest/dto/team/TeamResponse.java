package com.launchgate.contest.dto.team;

import java.util.List;

public record TeamResponse(
        Long id,
        Long contestId,
        Long leaderId,
        String name,
        String inviteToken,
        List<Long> memberIds
) {
}
