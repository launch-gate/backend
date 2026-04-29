package com.launchgate.contest.dto.stage;

import com.launchgate.contest.dto.FieldParticipantResponse;
import com.launchgate.contest.dto.resources.ResourceResponse;
import com.launchgate.contest.entity.ScoreScale;
import java.time.Instant;
import java.util.List;

public record StageParticipantResponse(
        Long id,
        int order,
        String title,
        String description,
        String rules,
        Instant deadlineAt,
        boolean eliminating,
        ScoreScale scoreScale,
        List<FieldParticipantResponse> fields,
        List<ResourceResponse> resources
) {
}
