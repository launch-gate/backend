package com.launchgate.submission.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

/**
 * Результат создания рабочего пространства проекта.
 * @param id идентификатор проекта
 * @param contestId идентификатор конкурса
 * @param teamId идентификатор команды
 * @param ownerParticipantId идентификатор владельца проекта (участника) для индивидуального конкурса
 * @param stages стадии проекта
 */
@Schema(description = "Рабочее пространство проекта со всеми этапами конкурса")
public record ProjectResponse(

        @Schema(description = "Идентификатор проекта", example = "18")
        Long id,

        @Schema(description = "Идентификатор конкурса", example = "12")
        Long contestId,

        @Schema(description = "Идентификатор команды", example = "5")
        Long teamId,

        @Schema(description = "Идентификатор владельца проекта (участника) для индивидуального конкурса", example = "21")
        Long ownerParticipantId,

        List<StageSubmissionResponse> stages
) {
}
