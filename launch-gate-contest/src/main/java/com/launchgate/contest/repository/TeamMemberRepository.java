package com.launchgate.contest.repository;

import java.util.List;

import com.launchgate.contest.entity.team.TeamMember;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamMemberRepository extends JpaRepository<TeamMember, Long> {
    List<TeamMember> findAllByTeam_Id(Long teamId);

    List<TeamMember> findAllByParticipant_Id(Long participantId);

    boolean existsByTeam_IdAndParticipant_Id(Long teamId, Long participantId);

    boolean existsByParticipant_IdAndTeam_Contest_Id(Long participantId, Long contestId);

    default List<TeamMember> findAllByTeamId(Long teamId) {
        return findAllByTeam_Id(teamId);
    }

    default List<TeamMember> findAllByParticipantId(Long participantId) {
        return findAllByParticipant_Id(participantId);
    }

    default boolean existsByTeamIdAndParticipantId(Long teamId, Long participantId) {
        return existsByTeam_IdAndParticipant_Id(teamId, participantId);
    }

    default boolean existsByParticipantIdAndContestId(Long participantId, Long contestId) {
        return existsByParticipant_IdAndTeam_Contest_Id(participantId, contestId);
    }
}
