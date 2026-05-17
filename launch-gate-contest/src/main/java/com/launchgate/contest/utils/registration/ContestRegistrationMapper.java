package com.launchgate.contest.utils.registration;

import com.launchgate.contest.dto.registration.ContestParticipantOrganizerResponse;
import com.launchgate.contest.dto.registration.ContestParticipantResponse;
import com.launchgate.contest.entity.ContestRegistration;
import com.launchgate.identity.entity.UserAccount;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ContestRegistrationMapper {

    public static ContestParticipantResponse toParticipantResponse(ContestRegistration registration) {
        UserAccount participant = registration.getParticipant();
        return new ContestParticipantResponse(
                participant.getId(),
                participant.getFullName(),
                participant.getNickname(),
                participant.getBio(),
                registration.getRegisteredAt()
        );
    }

    public static ContestParticipantOrganizerResponse toOrganizerResponse(ContestRegistration registration) {
        UserAccount participant = registration.getParticipant();
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
