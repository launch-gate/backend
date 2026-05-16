package com.launchgate.mentoring.repository;

import com.launchgate.mentoring.entity.*;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MentorCommentRepository extends JpaRepository<MentorComment, Long> {
    List<MentorComment> findAllByStageSubmissionIdOrderByCreatedAtDesc(Long stageSubmissionId);
}
