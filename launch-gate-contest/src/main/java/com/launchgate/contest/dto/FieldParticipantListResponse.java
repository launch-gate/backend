package com.launchgate.contest.dto;

import java.util.List;

/**
 * Список полей
 *
 * @param fields
 */
public record FieldParticipantListResponse(
        List<FieldParticipantResponse> fields
) {
}
