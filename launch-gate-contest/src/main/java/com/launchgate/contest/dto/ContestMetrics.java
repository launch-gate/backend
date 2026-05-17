package com.launchgate.contest.dto;

/**
 * Информация о метриках.
 *
 * @param registrations количество регистраций.
 * @param teams         количество команд.
 * @param stages        количество стадий.
 */
public record ContestMetrics(
        long registrations,
        long teams,
        long stages
) {
}
