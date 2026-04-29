package com.launchgate.submission.dto;

import com.launchgate.contest.dto.stage.StageParticipantResponse;
import com.launchgate.submission.entity.SubmissionStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "Stage submission draft or final submission")
public record StageSubmissionResponse(
        @Schema(description = "Stage submission id", example = "44")
        Long id,
        @Schema(description = "Current submission status", example = "DRAFT")
        SubmissionStatus status,
        StageParticipantResponse stage,
        List<ValueResponse> values
) {
}
