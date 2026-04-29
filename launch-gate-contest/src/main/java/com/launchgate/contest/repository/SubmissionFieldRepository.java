package com.launchgate.contest.repository;

import com.launchgate.contest.entity.*;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubmissionFieldRepository extends JpaRepository<SubmissionField, Long> {
    List<SubmissionField> findAllByStage_IdOrderByOrderAsc(Long stageId);

    void deleteAllByStage_Id(Long stageId);

    default List<SubmissionField> findAllByStageIdOrderByOrderAsc(Long stageId) {
        return findAllByStage_IdOrderByOrderAsc(stageId);
    }

    default void deleteAllByStageId(Long stageId) {
        deleteAllByStage_Id(stageId);
    }
}
