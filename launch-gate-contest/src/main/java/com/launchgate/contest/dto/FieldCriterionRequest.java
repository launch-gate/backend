package com.launchgate.contest.dto;

import jakarta.validation.constraints.NotBlank;

public record FieldCriterionRequest(
        Integer order,
        @NotBlank String description
) {
}
