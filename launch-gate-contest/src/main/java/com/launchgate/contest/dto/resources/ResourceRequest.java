package com.launchgate.contest.dto.resources;

import com.launchgate.contest.entity.ResourceType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Contest or stage resource")
public record ResourceRequest(
        @Schema(description = "Resource order inside the section. If omitted on create, resource is appended", example = "2")
        Integer order,
        @Schema(description = "Resource type", example = "LINK")
        @NotNull ResourceType type,
        @Schema(description = "Resource title", example = "Pitch deck template")
        @NotBlank String title,
        String description,
        @Schema(description = "External URL for link resources", example = "https://example.com/template")
        String linkUrl,
        @Schema(description = "Uploaded file id for file resources", example = "55")
        Long fileId
) {
}
