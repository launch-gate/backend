package com.launchgate.evaluation.service;

import com.launchgate.evaluation.dto.AssignmentResponse;
import com.launchgate.evaluation.dto.ReviewResponse;
import com.launchgate.evaluation.entity.ReviewAssignment;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class EvaluationMapper {

    public static AssignmentResponse toAssignmentResponse(ReviewAssignment assignment) {
        return new AssignmentResponse(
                assignment.getId(),
                assignment.getStageId(),
                assignment.getSubmissionId(),
                assignment.getExpertId(),
                assignment.getStatus()
        );
    }

    public static ReviewResponse toReviewResponse(ReviewAssignment assignment) {
        return new ReviewResponse(
                assignment.getId(),
                assignment.getSubmissionId(),
                assignment.getExpertId(),
                assignment.getStatus(),
                assignment.getScore(),
                assignment.getComment(),
                assignment.getFinalizedAt()
        );
    }
}
