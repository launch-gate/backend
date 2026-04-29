package com.launchgate.mentoring.dto;

import java.util.List;

public record MentorCommentListResponse(
        List<MentorCommentResponse> comments
) {
}
