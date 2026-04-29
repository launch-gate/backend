package com.launchgate.submission.repository;

import com.launchgate.submission.entity.*;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StageSubmissionRepository extends JpaRepository<StageSubmission, Long> {
    Optional<StageSubmission> findByProjectIdAndStageId(Long projectId, Long stageId);

    List<StageSubmission> findAllByStageId(Long stageId);

    long countByStageIdAndStatus(Long stageId, SubmissionStatus status);
}
