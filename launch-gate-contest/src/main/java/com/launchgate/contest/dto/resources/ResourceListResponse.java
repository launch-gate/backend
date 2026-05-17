package com.launchgate.contest.dto.resources;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/**
 * Коллекция ресурсов конкурса.
 *
 * @param resources список ресурсов.
 */
@Schema(description = "Коллекция ресурсов конкурса")
public record ResourceListResponse(
        @Schema(description = "Ресурсы, настроенные для конкурса или этапа")
        List<ResourceResponse> resources
) {
}
