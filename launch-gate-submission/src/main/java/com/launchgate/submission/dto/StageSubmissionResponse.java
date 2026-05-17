package com.launchgate.submission.dto;

import com.launchgate.contest.dto.stage.StageParticipantResponse;
import com.launchgate.submission.entity.SubmissionStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

/**
 * Форма этапа.
 * @param id идентификатор формы этапа
 * @param status статус формы
 * @param stage стадия
 * @param values значения формы
 */
@Schema(description = "Формы этапа")
public record StageSubmissionResponse(
        @Schema(description = "Идентификатор формы этапа", example = "44")
        Long id,

        @Schema(description = "Текущий статус формы", example = "DRAFT")
        SubmissionStatus status,

        StageParticipantResponse stage,

        List<ValueResponse> values
) {
}
