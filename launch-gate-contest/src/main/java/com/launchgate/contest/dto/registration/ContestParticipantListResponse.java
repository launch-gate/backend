package com.launchgate.contest.dto.registration;

import java.util.List;

public record ContestParticipantListResponse(
        List<ContestParticipantResponse> participants
) {
}
