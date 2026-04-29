package com.launchgate.contest.utils.registration;

import com.launchgate.contest.dto.registration.ContestParticipantOrganizerResponse;
import com.launchgate.contest.dto.registration.ContestParticipantResponse;
import com.launchgate.contest.entity.ContestRegistration;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ContestRegistrationMapper {

    public static ContestParticipantResponse toParticipantResponse(ContestRegistration registration) {
        var participant = registration.getParticipant();
        return new ContestParticipantResponse(
                participant.getId(),
                participant.getFullName(),
                participant.getNickname(),
                participant.getBio(),
                registration.getRegisteredAt()
        );
    }

    public static ContestParticipantOrganizerResponse toOrganizerResponse(ContestRegistration registration) {
        var participant = registration.getParticipant();
        return new ContestParticipantOrganizerResponse(
                participant.getId(),
                participant.getEmail(),
                participant.getFullName(),
                participant.getNickname(),
                participant.getBio(),
                registration.getRegisteredAt()
        );
    }
}
