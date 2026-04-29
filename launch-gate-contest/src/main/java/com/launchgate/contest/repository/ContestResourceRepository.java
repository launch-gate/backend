package com.launchgate.contest.repository;

import com.launchgate.contest.entity.ContestResource;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContestResourceRepository extends JpaRepository<ContestResource, Long> {
    List<ContestResource> findAllByStage_IdOrderByOrderAsc(Long stageId);

    void deleteAllByStage_Id(Long stageId);

    default List<ContestResource> findAllByStageIdOrderByOrderAsc(Long stageId) {
        return findAllByStage_IdOrderByOrderAsc(stageId);
    }

    default void deleteAllByStageId(Long stageId) {
        deleteAllByStage_Id(stageId);
    }
}
