package com.launchgate.mentoring.dto;

import jakarta.validation.constraints.NotNull;

import java.time.Instant;

/**
 * Данные запроса на планирование созвона с ментором.
 *
 * @param teamId   идентификатор команды.
 * @param startsAt дата начала встречи.
 * @param endsAt   дата конца встерчи.
 * @param link     ссылка на встречу.
 * @param notes    заметка к встречи.
 */
public record ScheduleCallRequest(
        @NotNull Long teamId,
        @NotNull Instant startsAt,
        @NotNull Instant endsAt,
        String link,
        String notes
) {
}
