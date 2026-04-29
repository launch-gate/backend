package com.launchgate.evaluation.dto;

import com.launchgate.evaluation.entity.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

public record AssignmentResponse(
        Long id,
        Long stageId,
        Long submissionId,
        Long expertId,
        ReviewStatus status
) {
}
