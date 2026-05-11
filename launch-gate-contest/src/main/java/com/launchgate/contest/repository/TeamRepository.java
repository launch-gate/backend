package com.launchgate.contest.repository;

import java.util.List;
import java.util.Optional;

import com.launchgate.contest.entity.team.Team;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamRepository extends JpaRepository<Team, Long> {
    List<Team> findAllByContest_IdOrderByCreatedAtDesc(Long contestId);

    Optional<Team> findByInviteToken(String inviteToken);

    boolean existsByInviteToken(String inviteToken);

    long countByContest_Id(Long contestId);

    default List<Team> findAllByContestIdOrderByCreatedAtDesc(Long contestId) {
        return findAllByContest_IdOrderByCreatedAtDesc(contestId);
    }

    default long countByContestId(Long contestId) {
        return countByContest_Id(contestId);
    }
}
