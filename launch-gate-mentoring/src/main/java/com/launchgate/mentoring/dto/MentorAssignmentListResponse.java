package com.launchgate.mentoring.dto;

import java.util.List;

/**
 * Список назначений менторов.
 * @param assignments назначения
 */
public record MentorAssignmentListResponse(
        List<MentorAssignmentResponse> assignments
) {
}
