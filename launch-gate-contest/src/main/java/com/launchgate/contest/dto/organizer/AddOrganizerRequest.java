package com.launchgate.contest.dto.organizer;

import com.launchgate.contest.entity.*;

import jakarta.validation.constraints.NotNull;

public record AddOrganizerRequest(
        @NotNull Long userId,
        @NotNull ContestRole role
) {
}
