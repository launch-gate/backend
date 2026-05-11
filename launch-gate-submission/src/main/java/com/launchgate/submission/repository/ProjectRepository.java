package com.launchgate.submission.repository;

import com.launchgate.submission.entity.*;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    Optional<Project> findByContest_IdAndTeam_Id(Long contestId, Long teamId);

    Optional<Project> findByContest_IdAndOwnerParticipant_Id(Long contestId, Long ownerParticipantId);

    List<Project> findAllByContest_Id(Long contestId);

    List<Project> findAllByOwnerParticipant_IdOrderByCreatedAtDesc(Long ownerParticipantId);

    List<Project> findAllByTeam_IdInOrderByCreatedAtDesc(List<Long> teamIds);
}
