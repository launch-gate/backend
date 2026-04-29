package com.launchgate.mentoring.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;

public record ScheduleCallRequest(
        @NotNull Long teamId,
        @NotNull Instant startsAt,
        @NotNull Instant endsAt,
        String link,
        String notes
) {
}
