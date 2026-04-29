package com.launchgate.evaluation.repository;

import com.launchgate.evaluation.entity.*;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AiReviewRepository extends JpaRepository<AiReview, Long> {
}
