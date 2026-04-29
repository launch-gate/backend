package com.launchgate.contest.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Deletion result")
public record DeletedResponse(
        @Schema(description = "Deleted entity id", example = "17")
        Long id
) {
}
