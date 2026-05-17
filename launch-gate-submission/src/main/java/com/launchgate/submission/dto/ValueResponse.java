package com.launchgate.submission.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Сохраненное значение поля.
 * @param id идентификатор сохраненного значения.
 * @param fieldId идентификатор поля.
 * @param valueText текстовое значение.
 * @param fileIds список идентификаторов загруженных файлов через запятую.
 */
@Schema(description = "Сохраненное значение поля")
public record ValueResponse(
        @Schema(description = "Идентификатор сохраненного значения", example = "900")
        Long id,
        @Schema(description = "Идентификатор поля", example = "101")
        Long fieldId,
        String valueText,
        String fileIds
) {
}
