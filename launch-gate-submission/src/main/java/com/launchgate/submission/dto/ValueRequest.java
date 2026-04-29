package com.launchgate.submission.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Single field value inside a stage form")
public record ValueRequest(
        @Schema(description = "Submission field id", example = "101")
        @NotNull Long fieldId,
        @Schema(description = "Text value when field is textual")
        String valueText,
        @Schema(description = "Comma-separated uploaded file ids for file fields", example = "55,56")
        String fileIds
) {
}
