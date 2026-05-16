package com.launchgate.evaluation.service.impl;

import com.launchgate.evaluation.dto.ReviewSummary;
import com.launchgate.evaluation.entity.ReviewAssignment;
import com.launchgate.evaluation.repository.ReviewAssignmentRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Objects;

import com.launchgate.evaluation.service.EvaluationReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Реализация сервис для формирования сводных отчетов по экспертным рецензиям.
 */
@Service
@RequiredArgsConstructor
public class EvaluationReviewServiceImpl implements EvaluationReviewService {
    private final ReviewAssignmentRepository reviewRepository;

    @Override
    @Transactional(readOnly = true)
    public ReviewSummary summary(Long submissionId) {

        List<ReviewAssignment> reviews = reviewRepository.findAllBySubmissionId(submissionId)
                .stream()
                .filter(review -> Objects.nonNull(review.getScore()))
                .toList();

        BigDecimal total = reviews
                .stream()
                .map(ReviewAssignment::getScore)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal average = reviews.isEmpty()
                ? BigDecimal.ZERO
                : total.divide(BigDecimal.valueOf(reviews.size()), 2, RoundingMode.HALF_UP);

        return new ReviewSummary(submissionId, average, reviews.size());
    }
}
