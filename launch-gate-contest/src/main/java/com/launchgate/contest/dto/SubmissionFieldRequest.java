package com.launchgate.contest.dto;

import com.launchgate.contest.enums.FieldType;
import com.launchgate.contest.enums.SubmissionFieldFileFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

/**
 * Запрос на создание или обновление одиночного поля ввода в форме этапа.
 *
 * @param order           порядковый номер поля в форме
 * @param title           название поля
 * @param type            тип поля
 * @param required        флаг обязательности заполнения поля
 * @param fileFormats     список разрешенных форматов файлов
 * @param maxFileSizeMb   лимит на размер файла в мегабайтах
 * @param participantHint подсказка для участника, отображаемая под полем ввода
 * @param exampleValue    пример ожидаемого ответа
 * @param expertNote      внутренняя заметка, видимая только организаторам и экспертам
 * @param criteria        список критериев оценки для данного поля
 */
@Schema(description = "Запрос на создание или обновление одиночного поля ввода в форме этапа")
public record SubmissionFieldRequest(
        @Schema(description = "Порядковый номер поля в форме", example = "3")
        Integer order,
        @Schema(description = "Название поля", example = "Problem statement")
        @NotBlank String title,
        @Schema(description = "Тип поля", example = "TEXT")
        @NotNull FieldType type,
        @Schema(description = "Флаг обязательности заполнения поля", example = "TEXT")
        Boolean required,
        @Schema(description = "Список разрешенных форматов файлов", example = "[\"PDF\",\"DOCX\"]")
        List<SubmissionFieldFileFormat> fileFormats,
        @Schema(description = "Лимит на размер файла в мегабайтах", example = "25")
        Integer maxFileSizeMb,
        @Schema(description = "Подсказка для участника, отображаемая под полем ввода")
        String participantHint,
        @Schema(description = "Пример ожидаемого ответа", example = "Describe the user problem in one paragraph")
        String exampleValue,
        @Schema(description = "Внутренняя заметка, видимая только организаторам и экспертам")
        String expertNote,
        @Schema(description = "Список критериев оценки для данного поля")
        @Valid List<FieldCriterionRequest> criteria
) {
}
