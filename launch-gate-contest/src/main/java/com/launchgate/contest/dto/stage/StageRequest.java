package com.launchgate.contest.dto.stage;

import com.launchgate.contest.entity.*;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;

@Schema(description = "Contest stage creation payload")
public record StageRequest(
        @Schema(description = "Stage title", example = "Problem validation")
        @NotBlank String title,
        String description,
        String rules,
        String extraInfo,
        @Schema(description = "Stage deadline in UTC", example = "2026-05-10T18:00:00Z")
        Instant deadlineAt,
        boolean eliminating,
        @Schema(description = "Stage scoring scale", example = "HUNDRED")
        @NotNull ScoreScale scoreScale,
        @Schema(description = "Optional position inside contest. If omitted, stage is appended", example = "2")
        Integer order
) {
}
