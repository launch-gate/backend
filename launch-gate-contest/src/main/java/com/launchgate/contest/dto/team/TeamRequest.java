package com.launchgate.contest.dto.team;

import jakarta.validation.constraints.NotBlank;

/**
 * Запрос на создание команды.
 * @param name название команды.
 */
public record TeamRequest(
        @NotBlank String name
) {
}
