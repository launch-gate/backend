package com.launchgate.mentoring.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Mentor comment for a stage submission")
public record MentorCommentRequest(
        @Schema(description = "Stage submission id", example = "19")
        @NotNull Long stageSubmissionId,
        @Schema(description = "Comment text", example = "Please strengthen the customer validation block before the next checkpoint.")
        @NotBlank String text
) {
}
