package com.launchgate.contest.dto.resources;

import com.launchgate.contest.enums.ResourceType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Запрос на создание ресурса.
 *
 * @param order       позиция ресурса
 * @param type        тип ресурса
 * @param title       название ресурса
 * @param description описание ресурса
 * @param linkUrl     внешняя ссылка для ресурсов
 * @param fileId      идентификатор загруженного файла
 */
@Schema(description = "Запрос на создание ресурса")
public record ResourceRequest(
        @Schema(description = "Порядок ресурса внутри секции", example = "2")
        Integer order,
        @Schema(description = "Тип ресурса", example = "LINK")
        @NotNull
        ResourceType type,
        @Schema(description = "Название ресурса", example = "ресурс")
        @NotBlank
        String title,
        @Schema(description = "Описание ресурса", example = "описание ресурса")
        String description,
        @Schema(description = "Внешняя ссылка для ресурсов", example = "https://example.com/template")
        String linkUrl,
        @Schema(description = "Идентификатор загруженного файла для ресурсов типа", example = "55")
        Long fileId
) {
}
