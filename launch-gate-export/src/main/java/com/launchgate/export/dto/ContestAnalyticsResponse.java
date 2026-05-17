package com.launchgate.export.dto;

/**
 * Аналитический отчет по конкурсу.
 *
 * @param registrations  Количество участников.
 * @param teams          Количество команд.
 * @param stages         Количество этапов.
 * @param submittedWorks Количество поданных работ.
 */
public record ContestAnalyticsResponse(
        Long registrations,
        Long teams,
        Long stages,
        Long submittedWorks
) {
}
