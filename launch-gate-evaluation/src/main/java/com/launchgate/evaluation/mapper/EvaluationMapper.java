package com.launchgate.evaluation.mapper;

import com.launchgate.evaluation.dto.AssignmentResponse;
import com.launchgate.evaluation.dto.ReviewResponse;
import com.launchgate.evaluation.entity.ReviewAssignment;
import lombok.experimental.UtilityClass;

@UtilityClass
public class EvaluationMapper {

    public AssignmentResponse toAssignmentResponse(ReviewAssignment assignment) {
        return new AssignmentResponse(
                assignment.getId(),
                assignment.getStage().getId(),
                assignment.getSubmission().getId(),
                assignment.getExpert().getId(),
                assignment.getStatus()
        );
    }

    public ReviewResponse toReviewResponse(ReviewAssignment assignment) {
        return new ReviewResponse(
                assignment.getId(),
                assignment.getSubmission().getId(),
                assignment.getExpert().getId(),
                assignment.getStatus(),
                assignment.getScore(),
                assignment.getComment(),
                assignment.getFinalizedAt()
        );
    }
}
