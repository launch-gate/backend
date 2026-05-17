package com.launchgate.mentoring.dto;

import java.util.List;

/**
 * Список назначенных встреч.
 *
 * @param calls встречи.
 */
public record MentorCallListResponse(
        List<MentorCallResponse> calls
) {
}
