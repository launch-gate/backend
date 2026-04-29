package com.launchgate.submission.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "Project workspace with all contest stages")
public record ProjectResponse(
        @Schema(description = "Project id", example = "18")
        Long id,
        @Schema(description = "Contest id", example = "12")
        Long contestId,
        @Schema(description = "Team id when project belongs to team", example = "5")
        Long teamId,
        @Schema(description = "Owner participant id for individual contest", example = "21")
        Long ownerParticipantId,
        List<StageSubmissionResponse> stages
) {
}
