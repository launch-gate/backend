package com.launchgate.submission.repository;

import com.launchgate.submission.entity.*;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubmissionValueRepository extends JpaRepository<SubmissionValue, Long> {
    List<SubmissionValue> findAllBySubmission_Id(Long submissionId);

    Optional<SubmissionValue> findBySubmission_IdAndField_Id(Long submissionId, Long fieldId);
}
