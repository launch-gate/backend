package com.launchgate.mentoring.dto;

import java.util.List;

/**
 * Список комментариев ментора.
 *
 * @param comments комментарии.
 */
public record MentorCommentListResponse(
        List<MentorCommentResponse> comments
) {
}
