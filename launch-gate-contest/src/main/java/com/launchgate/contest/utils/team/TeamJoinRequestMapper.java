package com.launchgate.contest.utils.team;

import com.launchgate.contest.dto.team.TeamJoinRequestResponse;
import com.launchgate.contest.entity.team.TeamJoinRequest;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TeamJoinRequestMapper {

    public static TeamJoinRequestResponse toResponse(TeamJoinRequest joinRequest) {
        var participant = joinRequest.getParticipant();
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
