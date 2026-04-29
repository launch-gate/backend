package com.launchgate.evaluation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;

@Schema(description = "Draft review payload")
public record ReviewDraftRequest(
        @Schema(description = "Score for the stage criterion", example = "86.50")
        BigDecimal score,
        @Schema(description = "Expert comment for the submission", example = "Strong solution, but the metrics section needs more evidence.")
        String comment
) {
}
