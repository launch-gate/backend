package com.launchgate.contest.dto;

import com.launchgate.contest.enums.FieldType;
import com.launchgate.contest.enums.SubmissionFieldFileFormat;

import java.util.List;

/**
 * Ответ с информацией о поле формы для участника.
 *
 * @param id              уникальный идентификатор поля
 * @param order           порядковый номер поля на форме
 * @param title           название поля
 * @param type            тип поля
 * @param required        флаг обязательности заполнения поля
 * @param fileFormats     список поддерживаемых форматов
 * @param maxFileSizeMb   максимальный размер файла в мегабайтах
 * @param participantHint подсказка для участника по заполнению поля
 * @param exampleValue    пример корректного значения для этого поля
 */
public record FieldParticipantResponse(
        Long id,
        int order,
        String title,
        FieldType type,
        boolean required,
        List<SubmissionFieldFileFormat> fileFormats,
        Integer maxFileSizeMb,
        String participantHint,
        String exampleValue
) {
}
