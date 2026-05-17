package com.launchgate.contest.repository;

import com.launchgate.contest.entity.*;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import com.launchgate.contest.enums.ContestRole;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContestOrganizerRepository extends JpaRepository<ContestOrganizer, Long> {
    List<ContestOrganizer> findAllByContest_Id(Long contestId);

    List<ContestOrganizer> findAllByUser_Id(Long userId);

    boolean existsByContest_IdAndUser_IdAndRoleIn(Long contestId, Long userId, Collection<ContestRole> roles);

    Optional<ContestOrganizer> findByContest_IdAndUser_IdAndRole(Long contestId, Long userId, ContestRole role);

    default List<ContestOrganizer> findAllByContestId(Long contestId) {
        return findAllByContest_Id(contestId);
    }

    default List<ContestOrganizer> findAllByUserId(Long userId) {
        return findAllByUser_Id(userId);
    }

    default boolean existsByContestIdAndUserIdAndRoleIn(Long contestId, Long userId, Collection<ContestRole> roles) {
        return existsByContest_IdAndUser_IdAndRoleIn(contestId, userId, roles);
    }

    default Optional<ContestOrganizer> findByContestIdAndUserIdAndRole(Long contestId, Long userId, ContestRole role) {
        return findByContest_IdAndUser_IdAndRole(contestId, userId, role);
    }
}
