package com.launchgate.submission.dto;

import com.launchgate.contest.dto.stage.StageOrganizesResponse;
import java.util.List;

/**
 * Список отправленных решений по этапу для пространства организатора.
 *
 * @param stage информация об этапе в пространстве организатора
 * @param submissions список отправленных решений
 */
public record OrganizerStageSubmissionListResponse(
        StageOrganizesResponse stage,
        List<OrganizerStageSubmissionResponse> submissions
) {
}
