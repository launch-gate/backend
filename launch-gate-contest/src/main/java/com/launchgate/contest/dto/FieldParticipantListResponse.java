package com.launchgate.contest.dto;

import java.util.List;

public record FieldParticipantListResponse(
        List<FieldParticipantResponse> fields
) {
}
