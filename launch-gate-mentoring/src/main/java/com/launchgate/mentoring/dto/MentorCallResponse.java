package com.launchgate.mentoring.dto;

import java.time.Instant;

/**
 * Информации о встрече с ментром.
 * @param id идентификатор встречи.
 * @param teamId идентификатор команды.
 * @param mentorId идентификатор ментора.
 * @param startsAt начало встречи.
 * @param endsAt конец встречи.
 * @param link ссылка на встречу.
 * @param notes заметка к встрече.
 */
public record MentorCallResponse(
        Long id,
        Long teamId,
        Long mentorId,
        Instant startsAt,
        Instant endsAt,
        String link,
        String notes
) {
}
