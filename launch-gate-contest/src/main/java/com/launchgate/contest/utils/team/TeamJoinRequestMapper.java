package com.launchgate.contest.utils.team;

import com.launchgate.contest.dto.team.TeamJoinRequestResponse;
import com.launchgate.contest.entity.team.TeamJoinRequest;
import com.launchgate.identity.entity.UserAccount;
import lombok.experimental.UtilityClass;

@UtilityClass
public class TeamJoinRequestMapper {

    public static TeamJoinRequestResponse toResponse(TeamJoinRequest joinRequest) {
        UserAccount participant = joinRequest.getParticipant();
        return new TeamJoinRequestResponse(
                joinRequest.getId(),
                joinRequest.getTeam().getId(),
                participant.getId(),
                participant.getFullName(),
                participant.getNickname(),
                joinRequest.getStatus(),
                joinRequest.getCreatedAt()
        );
    }
}
