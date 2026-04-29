package com.launchgate.contest.repository;

import java.util.List;
import java.util.Optional;

import com.launchgate.contest.entity.team.Team;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamRepository extends JpaRepository<Team, Long> {
    List<Team> findAllByContestIdOrderByCreatedAtDesc(Long contestId);

    Optional<Team> findByInviteToken(String inviteToken);

    boolean existsByInviteToken(String inviteToken);

    long countByContestId(Long contestId);
}
