package com.launchgate.mentoring.dto;

import jakarta.validation.constraints.NotNull;

/**
 * Запрос на назначение ментора для команды.
 *
 * @param teamId       идентификатор команды.
 * @param mentorUserId идентификатор ментора.
 */
public record AssignMentorRequest(
        @NotNull Long teamId,
        @NotNull Long mentorUserId
) {
}
