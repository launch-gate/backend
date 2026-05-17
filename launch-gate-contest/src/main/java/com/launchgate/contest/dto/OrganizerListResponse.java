package com.launchgate.contest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

/**
 * Ответ со списком организаторов конкурса.
 *
 * @param organizers список пользователей, привязанных к конкурсу с ролью организатора
 */
@Schema(description = "Информация об организаторах конкурса")
public record OrganizerListResponse(
        @Schema(description = "Список пользователей, привязанных к конкурсу с ролью организатора")
        List<OrganizerResponse> organizers
) {
}
