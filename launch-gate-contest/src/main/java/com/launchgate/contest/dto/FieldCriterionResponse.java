package com.launchgate.contest.dto;

/**
 * Ответ для настройки критерия.
 *
 * @param id          идентификатор.
 * @param order       позиция.
 * @param description описание.
 */
public record FieldCriterionResponse(
        Long id,
        Integer order,
        String description
) {
}
