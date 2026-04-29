package com.launchgate.contest.dto.stage;

import java.util.List;

public record StageOrganizesListResponse(
        List<StageOrganizesResponse> stages
) {
}
