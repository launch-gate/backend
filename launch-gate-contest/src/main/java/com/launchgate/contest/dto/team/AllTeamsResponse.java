package com.launchgate.contest.dto.team;

import java.util.List;

public record AllTeamsResponse(
        List<TeamResponse> teams
) {
}
