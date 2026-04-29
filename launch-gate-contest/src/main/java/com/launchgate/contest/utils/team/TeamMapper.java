package com.launchgate.contest.utils.team;

import com.launchgate.contest.dto.team.TeamResponse;
import com.launchgate.contest.entity.team.Team;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TeamMapper {

    public static TeamResponse toResponse(Team team, List<Long> memberIds) {
        return new TeamResponse(
                team.getId(),
                team.getContestId(),
                team.getLeaderId(),
                team.getName(),
                team.getInviteToken(),
                memberIds
        );
    }
}
