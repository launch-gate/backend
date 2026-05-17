package com.launchgate.submission.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

/**
 * Запрос на создания рабочего пространства проекта.
 * @param contestId идентификатор конкурса
 * @param teamId идентификатор команды
 */
@Schema(description = "Данные запроса для создания рабочей области проекта")
public record ProjectRequest(

        @Schema(description = "Идентификатор конкурса", example = "12")
        @NotNull
        Long contestId,

        @Schema(description = "Идентификатор команды для командного конкурса", example = "5")
        Long teamId
) {
}
