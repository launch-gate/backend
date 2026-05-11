package com.launchgate.evaluation.service;

import com.launchgate.common.DomainException;
import com.launchgate.common.ForbiddenException;
import com.launchgate.common.NotFoundException;
import com.launchgate.contest.entity.ContestRole;
import com.launchgate.contest.service.ContestReaderService;
import com.launchgate.contest.service.ContestRolePolicy;
import com.launchgate.evaluation.dto.AssignmentRequest;
import com.launchgate.evaluation.dto.AssignmentResponse;
import com.launchgate.evaluation.dto.ReviewDraftRequest;
import com.launchgate.evaluation.dto.ReviewResponse;
import com.launchgate.evaluation.entity.ReviewAssignment;
import com.launchgate.evaluation.entity.ReviewStatus;
import com.launchgate.evaluation.repository.ReviewAssignmentRepository;
import com.launchgate.identity.dto.AuthenticatedUser;
import com.launchgate.identity.service.UserService;
import com.launchgate.submission.entity.SubmissionStatus;
import com.launchgate.submission.service.SubmissionReaderService;
import java.time.Clock;
import java.time.Instant;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EvaluationService {
    private final ReviewAssignmentRepository assignmentRepository;
    private final ContestReaderService contestReaderService;
    private final ContestRolePolicy rolePolicy;
    private final SubmissionReaderService submissionReaderService;
    private final UserService userService;
    private final Clock clock;

    @Transactional
    public AssignmentResponse assign(AuthenticatedUser organizer, AssignmentRequest request) {
        var submission = submissionReaderService.getSubmissionById(request.submissionId());
        var stage = contestReaderService.getStageById(submission.getStageId());
        rolePolicy.requireAny(stage.getContestId(), organizer.id(), ContestRole.CREATOR, ContestRole.ADMIN);
        var expert = userService.getUserById(request.expertUserId());
        var expertId = expert.getId();
        if (!rolePolicy.hasAny(stage.getContestId(), expertId, ContestRole.EXPERT)) {
            throw new DomainException("expert_role_required", "Target user must have EXPERT role in contest");
        }
        if (submission.getStatus() != SubmissionStatus.SUBMITTED) {
            throw new DomainException("submission_not_ready", "Only submitted work can be assigned to expert");
        }
        var assignment = assignmentRepository.findBySubmission_IdAndExpert_Id(request.submissionId(), expertId)
                .orElseGet(() -> assignmentRepository.save(new ReviewAssignment(
                        stage,
                        submission,
                        expert,
                        Instant.now(clock)
                )));
        return EvaluationMapper.toAssignmentResponse(assignment);
    }

    @Transactional(readOnly = true)
    public List<AssignmentResponse> myAssignments(AuthenticatedUser expert, ReviewStatus status) {
        var assignments = status == null
                ? assignmentRepository.findAllByExpert_Id(expert.id())
                : assignmentRepository.findAllByExpert_IdAndStatus(expert.id(), status);
        return assignments.stream().map(EvaluationMapper::toAssignmentResponse).toList();
    }

    @Transactional
    public ReviewResponse saveDraft(AuthenticatedUser expert, Long assignmentId, ReviewDraftRequest request) {
        var assignment = requireExpertAssignment(expert, assignmentId);
        assignment.markDraft();
        assignment.update(request.score(), request.comment());
        return EvaluationMapper.toReviewResponse(assignment);
    }

    @Transactional
    public ReviewResponse publish(AuthenticatedUser expert, Long assignmentId) {
        var assignment = requireExpertAssignment(expert, assignmentId);
        if (assignment.getScore() == null) {
            throw new DomainException("score_required", "Review score is required");
        }
        assignment.complete();
        assignment.finalizeAt(Instant.now(clock));
        return EvaluationMapper.toReviewResponse(assignment);
    }

    @Transactional(readOnly = true)
    public com.launchgate.submission.dto.StageSubmissionResponse reviewSubmission(AuthenticatedUser expert, Long assignmentId) {
        var assignment = requireExpertAssignment(expert, assignmentId);
        return submissionReaderService.getSubmissionResponse(assignment.getSubmissionId());
    }

    private ReviewAssignment requireExpertAssignment(AuthenticatedUser expert, Long assignmentId) {
        var assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new NotFoundException("Review assignment not found"));
        if (!assignment.getExpertId().equals(expert.id())) {
            throw new ForbiddenException("Assignment belongs to another expert");
        }
        return assignment;
    }
}
