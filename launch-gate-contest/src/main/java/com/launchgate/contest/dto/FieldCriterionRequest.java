package com.launchgate.contest.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Запрос для настройки критерия.
 *
 * @param order       позиция.
 * @param description описание.
 */
public record FieldCriterionRequest(
        Integer order,
        @NotBlank
        String description
) {
}
