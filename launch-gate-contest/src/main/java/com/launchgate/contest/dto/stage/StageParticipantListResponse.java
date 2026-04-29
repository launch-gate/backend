package com.launchgate.contest.dto.stage;

import java.util.List;

public record StageParticipantListResponse(
        List<StageParticipantResponse> stages
) {
}
