package com.launchgate.mentoring.dto;

import java.util.List;

public record MentorAssignmentListResponse(
        List<MentorAssignmentResponse> assignments
) {
}
