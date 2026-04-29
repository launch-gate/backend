package com.launchgate.contest.repository;

import java.util.List;

import com.launchgate.contest.entity.stage.ContestStage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContestStageRepository extends JpaRepository<ContestStage, Long> {
    List<ContestStage> findAllByContest_IdOrderByOrderAsc(Long contestId);

    int countByContest_Id(Long contestId);

    default List<ContestStage> findAllByContestIdOrderByOrderAsc(Long contestId) {
        return findAllByContest_IdOrderByOrderAsc(contestId);
    }

    default int countByContestId(Long contestId) {
        return countByContest_Id(contestId);
    }
}
