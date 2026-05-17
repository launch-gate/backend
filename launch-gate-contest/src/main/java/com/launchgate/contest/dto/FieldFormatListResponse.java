package com.launchgate.contest.dto;

import java.util.List;

/**
 * Ответ со списком форматов полей.
 * @param formats спискок форматов полей
 */
public record FieldFormatListResponse(
        List<FieldFormatResponse> formats
) {
}
