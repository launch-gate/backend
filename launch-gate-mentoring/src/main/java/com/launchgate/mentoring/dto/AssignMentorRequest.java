package com.launchgate.mentoring.dto;

import jakarta.validation.constraints.NotNull;

public record AssignMentorRequest(
        @NotNull Long teamId,
        @NotNull Long mentorUserId
) {
}
