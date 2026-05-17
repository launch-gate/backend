package com.launchgate.contest.dto.registration;

import java.util.List;

/**
 * Ответ со списком организаторов конкурса.
 *
 * @param participants список организаторов
 */
public record ContestParticipantOrganizerListResponse(
        List<ContestParticipantOrganizerResponse> participants
) {
}
