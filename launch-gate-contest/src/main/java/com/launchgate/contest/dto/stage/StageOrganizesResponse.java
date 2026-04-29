package com.launchgate.contest.dto.stage;

import com.launchgate.contest.dto.FieldResponse;
import com.launchgate.contest.dto.resources.ResourceResponse;
import com.launchgate.contest.entity.ScoreScale;
import java.time.Instant;
import java.util.List;

public record StageOrganizesResponse(
        Long id,
        int order,
        String title,
        String description,
        String rules,
        String extraInfo,
        Instant deadlineAt,
        boolean eliminating,
        ScoreScale scoreScale,
        List<FieldResponse> fields,
        List<ResourceResponse> resources
) {
}
