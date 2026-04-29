package com.launchgate.submission.repository;

import com.launchgate.submission.entity.*;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    Optional<Project> findByContestIdAndTeamId(Long contestId, Long teamId);

    Optional<Project> findByContestIdAndOwnerParticipantId(Long contestId, Long ownerParticipantId);

    List<Project> findAllByContestId(Long contestId);

    List<Project> findAllByOwnerParticipantIdOrderByCreatedAtDesc(Long ownerParticipantId);

    List<Project> findAllByTeamIdInOrderByCreatedAtDesc(List<Long> teamIds);
}
