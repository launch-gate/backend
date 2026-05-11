package com.launchgate.aievaluation.repository;

import com.launchgate.aievaluation.entity.AiSubmissionReview;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AiSubmissionReviewRepository extends JpaRepository<AiSubmissionReview, Long> {
    Optional<AiSubmissionReview> findBySubmission_Id(Long submissionId);
}
