package com.launchgate.evaluation.service;

import com.launchgate.evaluation.dto.ReviewSummary;
import com.launchgate.evaluation.entity.ReviewAssignment;
import com.launchgate.evaluation.repository.ReviewAssignmentRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EvaluationCatalog {
    private final ReviewAssignmentRepository reviewRepository;

    @Transactional(readOnly = true)
    public ReviewSummary summary(Long submissionId) {
        var reviews = reviewRepository.findAllBySubmission_Id(submissionId).stream()
                .filter(review -> review.getScore() != null)
                .toList();
        var total = reviews.stream()
                .map(ReviewAssignment::getScore)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        var average = reviews.isEmpty()
                ? BigDecimal.ZERO
                : total.divide(BigDecimal.valueOf(reviews.size()), 2, RoundingMode.HALF_UP);
        return new ReviewSummary(submissionId, average, reviews.size());
    }
}
