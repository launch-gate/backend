package com.launchgate.contest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "Organizer collection")
public record OrganizerListResponse(
        @Schema(description = "Users attached to contest with organizer roles")
        List<OrganizerResponse> organizers
) {
}
