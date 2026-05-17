package com.launchgate.submission.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

/**
 * Значение одиночного поля внутри формы этапа.
 * @param fieldId идентификатор формы
 * @param valueText значение поля в текстовом формате.
 * @param fileIds список идентификаторов загруженных файлов через запятую.
 */
@Schema(description = "Значение одиночного поля внутри формы этапа")
public record ValueRequest(
        @Schema(description = "Идентификатор формы", example = "101")
        @NotNull
        Long fieldId,

        @Schema(description = "Значение поля в текстовом формате, если поле является текстовым")
        String valueText,

        @Schema(description = "Список идентификаторов загруженных файлов через запятую", example = "55,56")
        String fileIds
) {
}
