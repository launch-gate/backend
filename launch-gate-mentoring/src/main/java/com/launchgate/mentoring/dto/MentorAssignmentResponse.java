package com.launchgate.mentoring.dto;

/**
 * Результат на назначение ментора для команды.
 *
 * @param id        идентификатор назначения.
 * @param contestId идентификатор конкурса.
 * @param teamId    идентификатор команды.
 * @param mentorId  идентификатор ментора.
 */
public record MentorAssignmentResponse(
        Long id,
        Long contestId,
        Long teamId,
        Long mentorId
) {
}
