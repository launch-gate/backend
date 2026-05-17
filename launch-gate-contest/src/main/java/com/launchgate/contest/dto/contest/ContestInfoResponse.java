package com.launchgate.contest.dto.contest;

import com.launchgate.contest.enums.ContestStatus;
import com.launchgate.contest.enums.ParticipationMode;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

/**
 * Информация о конкурсе.
 *
 * @param id                 идентификатор конкурса
 * @param title              название конкурса
 * @param description        описание конкурса
 * @param status             статус конкурса
 * @param participationMode  тип конкурса
 * @param minTeamSize        минимальный размер команды
 * @param maxTeamSize        максимальный размер команды
 * @param registrationEndsAt дата и время окончания регистрации
 * @param teamBuildingEndsAt дата и время окончания командообразования
 * @param startsAt           дата и время начала конкурса
 * @param endsAt             дата и время окончания конкурса
 * @param contacts           список контактов
 */
public record ContestInfoResponse(
        @Schema(description = "Идентификатор конкурса", example = "12")
        Long id,
        @Schema(description = "Название конкурса")
        String title,
        @Schema(description = "Описание конкурса")
        String description,
        @Schema(description = "Текущий статус конкурса", example = "PUBLISHED")
        ContestStatus status,
        @Schema(description = "Тип конкурса", example = "TEAM")
        ParticipationMode participationMode,
        @Schema(description = "Минимальный размер команды")
        Integer minTeamSize,
        @Schema(description = "Максимальный размер команды")
        Integer maxTeamSize,
        @Schema(description = "Дата и время окончания регистрации")
        LocalDate registrationEndsAt,
        @Schema(description = "Дата и время окончания командообразования")
        LocalDate teamBuildingEndsAt,
        @Schema(description = "Дата и время начала конкурса")
        LocalDate startsAt,
        @Schema(description = "Дата и время окончания конкурса")
        LocalDate endsAt,
        @Schema(description = "Cписок контактов")
        String contacts
) {
}
