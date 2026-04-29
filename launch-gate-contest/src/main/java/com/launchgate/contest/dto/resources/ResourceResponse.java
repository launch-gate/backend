package com.launchgate.contest.dto.resources;

import com.launchgate.contest.entity.ResourceType;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Resource returned by API")
public record ResourceResponse(
        @Schema(description = "Resource id", example = "77")
        Long id,
        @Schema(description = "Ordering number", example = "1")
        int order,
        ResourceType type,
        String title,
        String description,
        String linkUrl,
        Long fileId
) {
}
