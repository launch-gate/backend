package com.launchgate.contest.dto.registration;

import java.util.List;

public record ContestParticipantOrganizerListResponse(
        List<ContestParticipantOrganizerResponse> participants
) {
}
