package com.launchgate.contest.dto.contest;

import com.launchgate.contest.entity.*;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

@Schema(description = "Contest creation or update payload")
public record ContestRequest(
        @Schema(description = "Contest title", example = "Launch Gate AI Challenge")
        @NotBlank String title,
        @Schema(description = "Short contest description")
        String description,
        @Schema(description = "Rules visible to participants")
        String rules,
        @Schema(description = "Individual or team contest", example = "TEAM")
        @NotNull ParticipationMode participationMode,
        Integer minTeamSize,
        Integer maxTeamSize,
        @Schema(description = "Registration closing date", example = "2026-05-20")
        LocalDate registrationEndsAt,
        @Schema(description = "Last day when team composition can change", example = "2026-05-18")
        LocalDate teamBuildingEndsAt,
        @Schema(description = "Contest start date", example = "2026-05-01")
        LocalDate startsAt,
        @Schema(description = "Contest end date", example = "2026-06-15")
        LocalDate endsAt,
        @Schema(description = "Organizer contact block")
        String contacts
) {
}
