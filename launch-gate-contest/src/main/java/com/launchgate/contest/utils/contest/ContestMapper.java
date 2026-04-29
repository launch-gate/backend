package com.launchgate.contest.utils.contest;

import com.launchgate.contest.dto.contest.ContestInfoResponse;
import com.launchgate.contest.entity.Contest;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ContestMapper {

    public static ContestInfoResponse toInfoResponse(Contest contest) {
        return new ContestInfoResponse(
                contest.getId(),
                contest.getTitle(),
                contest.getDescription(),
                contest.getStatus(),
                contest.getParticipationMode(),
                contest.getMinTeamSize(),
                contest.getMaxTeamSize(),
                contest.getRegistrationEndsAt(),
                contest.getTeamBuildingEndsAt(),
                contest.getStartsAt(),
                contest.getEndsAt(),
                contest.getContacts()
        );
    }
}
