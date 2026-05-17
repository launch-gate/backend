package com.launchgate.evaluation.dto;

import java.util.List;

/**
 * Назначенные проверки.
 *
 * @param assignments проверки.
 */
public record AssignmentListResponse(
        List<AssignmentResponse> assignments
) {
}
