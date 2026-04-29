package com.launchgate.mentoring.dto;

import java.util.List;

public record MentorCallListResponse(
        List<MentorCallResponse> calls
) {
}
