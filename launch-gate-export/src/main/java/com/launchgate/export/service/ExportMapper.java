package com.launchgate.export.service;

import com.launchgate.contest.entity.stage.ContestStage;
import com.launchgate.evaluation.dto.ReviewSummary;
import com.launchgate.export.dto.RankingRow;
import com.launchgate.submission.dto.SubmissionSummary;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ExportMapper {

    public static RankingRow toRankingRow(ContestStage stage, SubmissionSummary submission, ReviewSummary review) {
        return new RankingRow(
                stage.getTitle(),
                submission.solutionTitle(),
                submission.submissionId(),
                review.averageScore(),
                review.completedReviews()
        );
    }
}
