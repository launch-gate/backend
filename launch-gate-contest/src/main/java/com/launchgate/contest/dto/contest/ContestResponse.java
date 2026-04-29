package com.launchgate.contest.dto.contest;

import com.launchgate.contest.entity.*;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;

@Schema(description = "Contest overview")
public record ContestResponse(
        @Schema(description = "Contest id", example = "12")
        Long id,
        @Schema(description = "Contest title")
        String title,
        String description,
        @Schema(description = "Current contest status", example = "PUBLISHED")
        ContestStatus status,
        @Schema(description = "Participation mode", example = "TEAM")
        ParticipationMode participationMode,
        Integer minTeamSize,
        Integer maxTeamSize,
        LocalDate registrationEndsAt,
        LocalDate teamBuildingEndsAt,
        LocalDate startsAt,
        LocalDate endsAt,
        String contacts
) {
}
