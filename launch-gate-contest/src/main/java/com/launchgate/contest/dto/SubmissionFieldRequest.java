package com.launchgate.contest.dto;

import com.launchgate.contest.entity.*;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Single input field inside a stage form")
public record SubmissionFieldRequest(
        @Schema(description = "Field order inside the form. If omitted on create, field is appended", example = "3")
        Integer order,
        @Schema(description = "Field title", example = "Problem statement")
        @NotBlank String title,
        @Schema(description = "Field type", example = "TEXT")
        @NotNull FieldType type,
        boolean required,
        @Schema(description = "Allowed file extensions for file field", example = "pdf,pptx")
        String fileFormats,
        @Schema(description = "File size limit in megabytes", example = "25")
        Integer maxFileSizeMb,
        @Schema(description = "Serialized options for select field", example = "[\"B2B\",\"B2C\"]")
        String options,
        String participantHint,
        String exampleValue,
        String expertNote,
        String criteriaDescription
) {
}
