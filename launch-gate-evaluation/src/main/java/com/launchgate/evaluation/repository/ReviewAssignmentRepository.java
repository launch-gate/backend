package com.launchgate.evaluation.repository;

import com.launchgate.evaluation.entity.*;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewAssignmentRepository extends JpaRepository<ReviewAssignment, Long> {
    List<ReviewAssignment> findAllByExpertId(Long expertId);

    List<ReviewAssignment> findAllByExpertIdAndStatus(Long expertId, ReviewStatus status);

    List<ReviewAssignment> findAllBySubmissionId(Long submissionId);

    Optional<ReviewAssignment> findBySubmissionIdAndExpertId(Long submissionId, Long expertId);
}
