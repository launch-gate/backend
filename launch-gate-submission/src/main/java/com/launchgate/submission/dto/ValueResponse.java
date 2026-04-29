package com.launchgate.submission.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Saved field value")
public record ValueResponse(
        @Schema(description = "Value id", example = "900")
        Long id,
        @Schema(description = "Field id", example = "101")
        Long fieldId,
        String valueText,
        String fileIds
) {
}
