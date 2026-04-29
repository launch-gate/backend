package com.launchgate.evaluation.dto;

import com.launchgate.evaluation.entity.*;

import java.math.BigDecimal;

public record ReviewSummary(Long submissionId, BigDecimal averageScore, long completedReviews) {
}
