package com.launchgate.mentoring.dto;

import java.time.Instant;

public record MentorCallResponse(
        Long id,
        Long teamId,
        Long mentorId,
        Instant startsAt,
        Instant endsAt,
        String link,
        String notes
) {
}
