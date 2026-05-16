package com.launchgate.evaluation.service;

import com.launchgate.evaluation.dto.ReviewSummary;

/**
 * Сервис для формирования сводных отчетов по экспертным рецензиям.
 */
public interface EvaluationReviewService {

    /**
     * Формирует сводную информацию по оценкам для конкретного решения этапа.
     */
    ReviewSummary summary(Long submissionId);
}
