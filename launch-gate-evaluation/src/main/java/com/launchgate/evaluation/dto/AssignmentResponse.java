package com.launchgate.evaluation.dto;

import com.launchgate.evaluation.entity.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

/**
 * Ответ на запрос назначения эксперта.
 * @param id идентификатор назначения.
 * @param stageId идентификатор стадии.
 * @param submissionId идентификатор отправленного решения.
 * @param expertId идентификатор эксперта.
 * @param status статус.
 */
public record AssignmentResponse(
        Long id,
        Long stageId,
        Long submissionId,
        Long expertId,
        ReviewStatus status
) {
}
