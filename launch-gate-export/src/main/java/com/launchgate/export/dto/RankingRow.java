package com.launchgate.export.dto;

import java.math.BigDecimal;

public record RankingRow(String stage, String project, Long submissionId, BigDecimal score, long completedReviews) {
}
