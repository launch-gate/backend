package com.launchgate.contest.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Результат удаления.
 *
 * @param id идентификатор.
 */
@Schema(description = "Результат удаления")
public record DeletedResponse(
        @Schema(description = "Идентификатор", example = "17")
        Long id
) {
}
