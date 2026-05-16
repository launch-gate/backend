package com.launchgate.evaluation.service.impl;

import com.launchgate.common.ForbiddenException;
import com.launchgate.common.LaunchGateException;
import com.launchgate.common.NotFoundException;
import com.launchgate.contest.entity.ContestRole;
import com.launchgate.contest.entity.stage.ContestStage;
import com.launchgate.contest.service.ContestReaderService;
import com.launchgate.contest.service.ContestRolePolicy;
import com.launchgate.evaluation.dto.AssignmentRequest;
import com.launchgate.evaluation.dto.AssignmentResponse;
import com.launchgate.evaluation.dto.ReviewDraftRequest;
import com.launchgate.evaluation.dto.ReviewResponse;
import com.launchgate.evaluation.entity.ReviewAssignment;
import com.launchgate.evaluation.entity.ReviewStatus;
import com.launchgate.evaluation.repository.ReviewAssignmentRepository;
import com.launchgate.evaluation.mapper.EvaluationMapper;
import com.launchgate.evaluation.service.EvaluationService;
import com.launchgate.identity.dto.AuthenticatedUser;
import com.launchgate.identity.entity.UserAccount;
import com.launchgate.identity.service.UserService;
import com.launchgate.submission.dto.StageSubmissionResponse;
import com.launchgate.submission.entity.StageSubmission;
import com.launchgate.submission.entity.SubmissionStatus;
import com.launchgate.submission.service.SubmissionReaderService;
import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Objects;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Реализация формирования экспертной оценки.
 */
@Service
@RequiredArgsConstructor
public class EvaluationServiceImpl implements EvaluationService {
    private final ReviewAssignmentRepository assignmentRepository;
    private final ContestReaderService contestReaderService;
    private final ContestRolePolicy rolePolicy;
    private final SubmissionReaderService submissionReaderService;
    private final UserService userService;
    private final Clock clock;

    @Override
    @Transactional
    public AssignmentResponse assign(AuthenticatedUser organizer, AssignmentRequest request) {

        StageSubmission submission = submissionReaderService.getSubmissionById(request.submissionId());
        ContestStage stage = contestReaderService.getStageById(submission.getStageId());

        rolePolicy.requireAny(stage.getContestId(), organizer.id(), ContestRole.CREATOR, ContestRole.ADMIN);
        UserAccount expert = userService.getUserById(request.expertUserId());

        if (!rolePolicy.hasAny(stage.getContestId(), expert.getId(), ContestRole.EXPERT)) {
            throw new ForbiddenException("Пользователь должен иметь роль эксперта в конкурсе");
        }

        if (!SubmissionStatus.SUBMITTED.equals(submission.getStatus())) {
            throw new LaunchGateException("Назначить эксперта можно только на отправленную работу");
        }

        ReviewAssignment assignment = assignmentRepository.findBySubmissionIdAndExpertId(request.submissionId(), expert.getId())
                .orElseGet(() -> assignmentRepository.save(new ReviewAssignment(
                        stage,
                        submission,
                        expert,
                        Instant.now(clock)
                )));

        return EvaluationMapper.toAssignmentResponse(assignment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AssignmentResponse> myAssignments(AuthenticatedUser expert, ReviewStatus status) {
        List<ReviewAssignment> assignments = (status == null)
                ? assignmentRepository.findAllByExpertId(expert.id())
                : assignmentRepository.findAllByExpertIdAndStatus(expert.id(), status);

        return assignments.stream()
                .map(EvaluationMapper::toAssignmentResponse)
                .toList();
    }

    @Override
    @Transactional
    public ReviewResponse saveDraft(AuthenticatedUser expert, Long assignmentId, ReviewDraftRequest request) {

        ReviewAssignment assignment = requireExpertAssignment(expert, assignmentId);
        assignment.markDraft();
        assignment.update(request.score(), request.comment());

        return EvaluationMapper.toReviewResponse(assignment);
    }

    @Override
    @Transactional
    public ReviewResponse publish(AuthenticatedUser expert, Long assignmentId) {
        ReviewAssignment assignment = requireExpertAssignment(expert, assignmentId);

        if (Objects.nonNull(assignment.getScore())) {
            throw new LaunchGateException("Необходимо выставить оценку для публикации проверки");
        }

        assignment.complete();
        assignment.finalizeAt(Instant.now(clock));

        return EvaluationMapper.toReviewResponse(assignment);
    }

    @Override
    @Transactional(readOnly = true)
    public StageSubmissionResponse reviewSubmission(AuthenticatedUser expert, Long assignmentId) {

        ReviewAssignment assignment = requireExpertAssignment(expert, assignmentId);

        return submissionReaderService.getSubmissionResponse(assignment.getSubmissionId());
    }

    private ReviewAssignment requireExpertAssignment(AuthenticatedUser expert, Long assignmentId) {

        ReviewAssignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new NotFoundException("Назначение на проверку не найдено"));

        if (!assignment.getExpertId().equals(expert.id())) {
            throw new ForbiddenException("Проверка относится к другому эксперту");
        }

        return assignment;
    }
}
