package com.launchgate.submission.dto;

import com.launchgate.submission.entity.*;


public record SubmissionSummary(
        Long submissionId,
        Long projectId,
        Long stageId,
        Long contestId,
        String solutionTitle,
        SubmissionStatus status
) {
}
