package com.launchgate.export.mapper;

import com.launchgate.contest.entity.stage.ContestStage;
import com.launchgate.evaluation.dto.ReviewSummary;
import com.launchgate.export.dto.RankingRow;
import com.launchgate.submission.dto.SubmissionSummary;
import lombok.experimental.UtilityClass;

/**
 * Маппер для формирования отчетов
 */
@UtilityClass
public class ExportMapper {

    /**
     * Сформировать сроку турнирной таблицы.
     *
     * @return срока турнирной таблицы
     */
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
