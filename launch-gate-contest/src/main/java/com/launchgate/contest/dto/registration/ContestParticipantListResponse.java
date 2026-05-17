package com.launchgate.contest.dto.registration;

import java.util.List;

/**
 * Ответ со списком участников конкурса.
 *
 * @param participants список участников
 */
public record ContestParticipantListResponse(
        List<ContestParticipantResponse> participants
) {
}
