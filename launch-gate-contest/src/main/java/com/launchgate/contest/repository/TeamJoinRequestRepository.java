package com.launchgate.contest.repository;

import java.util.List;

import com.launchgate.contest.entity.team.TeamJoinRequest;
import com.launchgate.contest.enums.TeamJoinRequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamJoinRequestRepository extends JpaRepository<TeamJoinRequest, Long> {
    List<TeamJoinRequest> findAllByTeam_Id(Long teamId);

    List<TeamJoinRequest> findAllByTeam_IdAndStatusOrderByCreatedAtDesc(Long teamId, TeamJoinRequestStatus status);

    boolean existsByTeam_IdAndParticipant_IdAndStatus(Long teamId, Long participantId, TeamJoinRequestStatus status);

    default List<TeamJoinRequest> findAllByTeamId(Long teamId) {
        return findAllByTeam_Id(teamId);
    }

    default List<TeamJoinRequest> findAllByTeamIdAndStatusOrderByCreatedAtDesc(Long teamId, TeamJoinRequestStatus status) {
        return findAllByTeam_IdAndStatusOrderByCreatedAtDesc(teamId, status);
    }

    default boolean existsByTeamIdAndParticipantIdAndStatus(Long teamId, Long participantId, TeamJoinRequestStatus status) {
        return existsByTeam_IdAndParticipant_IdAndStatus(teamId, participantId, status);
    }
}
