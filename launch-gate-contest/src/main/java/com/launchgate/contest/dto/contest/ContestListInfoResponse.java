package com.launchgate.contest.dto.contest;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "Contest collection")
public record ContestListInfoResponse(
        @Schema(description = "Contests visible in current context")
        List<ContestInfoResponse> contests
) {
}
