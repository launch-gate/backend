package com.launchgate.submission.repository;

import com.launchgate.submission.entity.*;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StageSubmissionRepository extends JpaRepository<StageSubmission, Long> {
    Optional<StageSubmission> findByProject_IdAndStage_Id(Long projectId, Long stageId);

    List<StageSubmission> findAllByStage_Id(Long stageId);

    long countByStage_IdAndStatus(Long stageId, SubmissionStatus status);
}
