package com.launchgate.evaluation.repository;

import com.launchgate.evaluation.entity.*;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewAssignmentRepository extends JpaRepository<ReviewAssignment, Long> {
    List<ReviewAssignment> findAllByExpert_Id(Long expertId);

    List<ReviewAssignment> findAllByExpert_IdAndStatus(Long expertId, ReviewStatus status);

    List<ReviewAssignment> findAllBySubmission_Id(Long submissionId);

    Optional<ReviewAssignment> findBySubmission_IdAndExpert_Id(Long submissionId, Long expertId);
}
