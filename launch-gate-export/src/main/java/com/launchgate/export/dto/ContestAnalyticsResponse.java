package com.launchgate.export.dto;

import com.launchgate.export.entity.*;

import jakarta.validation.constraints.NotNull;

public record ContestAnalyticsResponse(
        long registrations,
        long teams,
        long stages,
        long submittedWorks
) {
}
