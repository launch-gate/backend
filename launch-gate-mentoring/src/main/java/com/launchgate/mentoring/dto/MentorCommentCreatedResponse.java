package com.launchgate.mentoring.dto;

/**
 * Результат создания комментария ментора.
 *
 * @param commentId идентификатор комментария.
 */
public record MentorCommentCreatedResponse(
        Long commentId
) {
}
