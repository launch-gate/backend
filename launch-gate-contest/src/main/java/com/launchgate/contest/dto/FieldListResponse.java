package com.launchgate.contest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

/**
 * Ответ со списком полей формы отправки решения.
 *
 * @param fields список настроенных полей для формы конкретного этапа
 */
@Schema(description = "Коллекция полей")
public record FieldListResponse(
        @Schema(description = "Поля, настроенные для формы этапа")
        List<FieldResponse> fields
) {
}
