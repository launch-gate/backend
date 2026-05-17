package com.launchgate.contest.dto.resources;

import com.launchgate.contest.enums.ResourceType;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Информация о ресурсе.
 *
 * @param id          идентификатор ресурса
 * @param order       позиция ресурса
 * @param type        тип ресурса
 * @param title       название ресурса
 * @param description описание ресурса
 * @param linkUrl     внешняя ссылка для ресурсов
 * @param fileId      идентификатор загруженного файла
 */
@Schema(description = "Resource returned by API")
public record ResourceResponse(
        @Schema(description = "Идентификатор ресурса", example = "77")
        Long id,
        @Schema(description = "Позиция ресурса", example = "1")
        Integer order,
        ResourceType type,
        String title,
        String description,
        String linkUrl,
        Long fileId
) {
}
