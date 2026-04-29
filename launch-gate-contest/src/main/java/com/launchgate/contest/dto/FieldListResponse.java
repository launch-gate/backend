package com.launchgate.contest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "Submission field collection")
public record FieldListResponse(
        @Schema(description = "Fields configured for a stage form")
        List<FieldResponse> fields
) {
}
