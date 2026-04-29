package com.launchgate.submission.repository;

import com.launchgate.submission.entity.*;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubmissionValueRepository extends JpaRepository<SubmissionValue, Long> {
    List<SubmissionValue> findAllBySubmissionId(Long submissionId);

    Optional<SubmissionValue> findBySubmissionIdAndFieldId(Long submissionId, Long fieldId);
}
