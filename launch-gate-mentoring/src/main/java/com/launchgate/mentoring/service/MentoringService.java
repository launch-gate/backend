package com.launchgate.mentoring.service;

import com.launchgate.common.DomainException;
import com.launchgate.common.ForbiddenException;
import com.launchgate.contest.entity.ContestRole;
import com.launchgate.contest.service.ContestRolePolicy;
import com.launchgate.contest.service.TeamService;
import com.launchgate.identity.dto.AuthenticatedUser;
import com.launchgate.identity.service.UserService;
import com.launchgate.mentoring.dto.AssignMentorRequest;
import com.launchgate.mentoring.dto.MentorAssignmentResponse;
import com.launchgate.mentoring.dto.MentorCallResponse;
import com.launchgate.mentoring.dto.MentorCommentRequest;
import com.launchgate.mentoring.dto.MentorCommentResponse;
import com.launchgate.mentoring.dto.ScheduleCallRequest;
import com.launchgate.mentoring.entity.MentorAssignment;
import com.launchgate.mentoring.entity.MentorCall;
import com.launchgate.mentoring.entity.MentorComment;
import com.launchgate.mentoring.repository.MentorAssignmentRepository;
import com.launchgate.mentoring.repository.MentorCallRepository;
import com.launchgate.mentoring.repository.MentorCommentRepository;
import com.launchgate.submission.service.SubmissionCatalog;
import java.time.Clock;
import java.time.Instant;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MentoringService {
    private final MentorAssignmentRepository assignmentRepository;
    private final MentorCommentRepository commentRepository;
    private final MentorCallRepository callRepository;
    private final TeamService teamService;
    private final ContestRolePolicy rolePolicy;
    private final UserService userService;
    private final SubmissionCatalog submissionCatalog;
    private final Clock clock;

    @Transactional
    public MentorAssignmentResponse assign(AuthenticatedUser organizer, AssignMentorRequest request) {
        var team = teamService.getTeam(request.teamId());
        rolePolicy.requireAny(team.getContestId(), organizer.id(), ContestRole.CREATOR, ContestRole.ADMIN);
        var mentorId = userService.findUserId(request.mentorUserId());
        if (!rolePolicy.hasAny(team.getContestId(), mentorId, ContestRole.MENTOR)) {
            throw new DomainException("mentor_role_required", "Target user must have MENTOR role in contest");
        }
        var assignment = assignmentRepository.findByTeamIdAndMentorId(team.getId(), mentorId)
                .orElseGet(() -> assignmentRepository.save(new MentorAssignment(
                        team.getContestId(),
                        team.getId(),
                        mentorId,
                        Instant.now(clock)
                )));
        return MentoringMapper.toResponse(assignment);
    }

    @Transactional(readOnly = true)
    public List<MentorAssignmentResponse> myTeams(AuthenticatedUser mentor) {
        return assignmentRepository.findAllByMentorId(mentor.id()).stream().map(MentoringMapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<MentorCallResponse> myCalls(AuthenticatedUser mentor) {
        return callRepository.findAllByMentorIdOrderByStartsAtAsc(mentor.id()).stream()
                .map(MentoringMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<MentorCommentResponse> comments(AuthenticatedUser user, Long stageSubmissionId) {
        var submission = submissionCatalog.requireSubmission(stageSubmissionId);
        var project = submissionCatalog.requireProject(submission.getProjectId());
        if (project.getTeamId() == null) {
            throw new ForbiddenException("Mentor comments are available only for team submissions");
        }
        var canRead = assignmentRepository.existsByTeamIdAndMentorId(project.getTeamId(), user.id())
                || teamService.isMember(project.getTeamId(), user.id());
        if (!canRead) {
            throw new ForbiddenException("No access to mentor comments");
        }
        return commentRepository.findAllByStageSubmissionIdOrderByCreatedAtDesc(stageSubmissionId).stream()
                .map(comment -> new MentorCommentResponse(
                        comment.getId(),
                        comment.getStageSubmissionId(),
                        comment.getMentorId(),
                        comment.getText(),
                        comment.getCreatedAt()
                ))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<MentorCallResponse> teamCalls(AuthenticatedUser user, Long teamId) {
        var canRead = assignmentRepository.existsByTeamIdAndMentorId(teamId, user.id())
                || teamService.isMember(teamId, user.id());
        if (!canRead) {
            throw new ForbiddenException("No access to mentor calls");
        }
        return callRepository.findAllByTeamIdOrderByStartsAtAsc(teamId).stream()
                .map(MentoringMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public com.launchgate.submission.dto.StageSubmissionResponse stageSubmission(AuthenticatedUser user, Long stageSubmissionId) {
        var submission = submissionCatalog.requireSubmission(stageSubmissionId);
        var project = submissionCatalog.requireProject(submission.getProjectId());
        if (project.getTeamId() == null) {
            throw new ForbiddenException("Mentor can open only team submissions");
        }
        var canRead = assignmentRepository.existsByTeamIdAndMentorId(project.getTeamId(), user.id())
                || teamService.isMember(project.getTeamId(), user.id());
        if (!canRead) {
            throw new ForbiddenException("No access to stage submission");
        }
        return submissionCatalog.submissionResponse(stageSubmissionId);
    }

    @Transactional
    public Long comment(AuthenticatedUser mentor, MentorCommentRequest request) {
        var submission = submissionCatalog.requireSubmission(request.stageSubmissionId());
        var project = submissionCatalog.requireProject(submission.getProjectId());
        if (project.getTeamId() == null || !assignmentRepository.existsByTeamIdAndMentorId(project.getTeamId(), mentor.id())) {
            throw new ForbiddenException("Mentor is not assigned to this team submission");
        }
        return commentRepository.save(new MentorComment(
                request.stageSubmissionId(),
                mentor.id(),
                request.text(),
                Instant.now(clock)
        )).getId();
    }

    @Transactional
    public Long scheduleCall(AuthenticatedUser mentor, ScheduleCallRequest request) {
        if (!assignmentRepository.existsByTeamIdAndMentorId(request.teamId(), mentor.id())) {
            throw new ForbiddenException("Mentor is not assigned to this team");
        }
        if (!request.endsAt().isAfter(request.startsAt())) {
            throw new DomainException("bad_call_time", "Call end time must be after start time");
        }
        return callRepository.save(new MentorCall(
                request.teamId(),
                mentor.id(),
                request.startsAt(),
                request.endsAt(),
                request.link(),
                request.notes()
        )).getId();
    }
}
