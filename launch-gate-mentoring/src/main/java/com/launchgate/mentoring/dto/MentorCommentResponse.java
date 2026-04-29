package com.launchgate.mentoring.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;

@Schema(description = "Mentor comment response")
public record MentorCommentResponse(
        @Schema(description = "Comment id", example = "71")
        Long id,
        @Schema(description = "Stage submission id", example = "19")
        Long stageSubmissionId,
        @Schema(description = "Mentor user id", example = "8")
        Long mentorId,
        @Schema(description = "Comment text")
        String text,
        @Schema(description = "Comment creation time")
        Instant createdAt
) {
}
