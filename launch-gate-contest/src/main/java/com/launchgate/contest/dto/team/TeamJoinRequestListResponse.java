package com.launchgate.contest.dto.team;

import java.util.List;

public record TeamJoinRequestListResponse(
        List<TeamJoinRequestResponse> requests
) {
}
