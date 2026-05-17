package com.launchgate.contest.dto.contest;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/**
 * Информация о всех конкурсах.
 *
 * @param contests список конкурсов.
 */
@Schema(description = "Информация о всех конкурсах")
public record ContestListInfoResponse(
        @Schema(description = "Конкурсы, видимые в текущем контексте")
        List<ContestInfoResponse> contests
) {
}
