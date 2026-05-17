package com.launchgate.mentoring.dto;

/**
 * Результат создания встречи с ментором.
 *
 * @param callId идентификатор встречи.
 */
public record MentorCallCreatedResponse(
        Long callId
) {
}
