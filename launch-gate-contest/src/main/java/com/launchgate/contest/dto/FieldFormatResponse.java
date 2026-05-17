package com.launchgate.contest.dto;

import com.launchgate.contest.enums.FieldFileFormatCategory;

/**
 * Ответ с информацией о поддерживаемом формате файлов.
 *
 * @param code            код формата
 * @param extension       расширение файла
 * @param category        категория формата
 * @param aiTextSupported true, если ИИ умеет извлекать/анализировать текст из этого формата
 */
public record FieldFormatResponse(
        String code,
        String extension,
        FieldFileFormatCategory category,
        Boolean aiTextSupported
) {
}
