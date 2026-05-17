package com.launchgate.contest.dto.contest;

import com.launchgate.contest.enums.ParticipationMode;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * Данные для создания или обновления конкурса.
 *
 * @param title               название конкурса
 * @param description         краткое описание конкурса
 * @param rules               правила проведения, регламент и критерии, видимые участникам
 * @param participationMode   формат участия в конкурсе
 * @param minTeamSize         минимально допустимое количество участников в команде
 * @param maxTeamSize         максимально допустимое количество участников в команде
 * @param registrationEndsAt  дата и время окончания приема заявок на регистрацию в конкурсе
 * @param teamBuildingEndsAt  крайний срок, до которого разрешено формировать команды и менять их состав
 * @param startsAt            дата начала проведения этапов конкурса
 * @param endsAt              дата авершения конкурса и подведения итогов
 * @param contacts            информационный блок с контактами организаторов
 */
@Schema(description = "Данные для создания или обновления конкурса")
public record ContestRequest(
        @Schema(description = "Название конкурса", example = "Launch Gate AI Challenge")
        @NotBlank String title,
        @Schema(description = "Краткое описание конкурса")
        String description,
        @Schema(description = "Правила проведения, регламент и критерии, видимые участникам")
        String rules,
        @Schema(description = "Формат участия в конкурсе", example = "TEAM")
        @NotNull
        ParticipationMode participationMode,
        @Schema(description = "Минимально допустимое количество участников в команде")
        Integer minTeamSize,
        @Schema(description = "Максимально допустимое количество участников в команде")
        Integer maxTeamSize,
        @Schema(description = "Дата и время окончания приема заявок на регистрацию в конкурсе", example = "2026-05-20")
        LocalDate registrationEndsAt,
        @Schema(description = "Крайний срок, до которого разрешено формировать команды и менять их состав", example = "2026-05-18")
        LocalDate teamBuildingEndsAt,
        @Schema(description = "Дата начало конкурса", example = "2026-05-01")
        LocalDate startsAt,
        @Schema(description = "Дата окончания конкурса", example = "2026-06-15")
        LocalDate endsAt,
        @Schema(description = "Контакты организаторов")
        String contacts
) {
}
