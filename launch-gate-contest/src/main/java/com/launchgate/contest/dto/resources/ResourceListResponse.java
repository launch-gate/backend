package com.launchgate.contest.dto.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "Resource collection")
public record ResourceListResponse(
        @Schema(description = "Resources configured for contest or stage")
        List<ResourceResponse> resources
) {
}
