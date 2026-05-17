package com.launchgate.submission.dto;

import com.launchgate.submission.entity.*;

/**
 * Краткая сводка по отправленной форме этапа.
 * @param submissionId идентификатор формы.
 * @param projectId идентификатор проекта.
 * @param stageId идентификатор стадии.
 * @param contestId идентификатор конкурса.
 * @param solutionTitle название решения.
 * @param status статус формы.
 */
public record SubmissionSummary(
        Long submissionId,
        Long projectId,
        Long stageId,
        Long contestId,
        String solutionTitle,
        SubmissionStatus status
) {
}
