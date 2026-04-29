package com.launchgate.evaluation.dto;

import java.util.List;

public record AssignmentListResponse(
        List<AssignmentResponse> assignments
) {
}
