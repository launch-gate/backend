package com.launchgate.mentoring.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;

public record MentorAssignmentResponse(
        Long id,
        Long contestId,
        Long teamId,
        Long mentorId
) {
}
