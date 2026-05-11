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
import com.launchgate.submission.service.SubmissionReaderService;
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
    private final SubmissionReaderService submissionReaderService;
    private final Clock clock;

    @Transactional
    public MentorAssignmentResponse assign(AuthenticatedUser organizer, AssignMentorRequest request) {
        var team = teamService.getTeam(request.teamId());
        rolePolicy.requireAny(team.getContestId(), organizer.id(), ContestRole.CREATOR, ContestRole.ADMIN);
        var mentor = userService.getUserById(request.mentorUserId());
        if (!rolePolicy.hasAny(team.getContestId(), mentor.getId(), ContestRole.MENTOR)) {
            throw new DomainException("mentor_role_required", "Target user must have MENTOR role in contest");
        }

        var assignment = assignmentRepository.findByTeam_IdAndMentor_Id(team.getId(), mentor.getId())
                .orElseGet(() -> assignmentRepository.save(new MentorAssignment(
                        team.getContest(),
                        team,
                        mentor,
                        Instant.now(clock)
                )));
        return MentoringMapper.toResponse(assignment);
    }

    @Transactional(readOnly = true)
    public List<MentorAssignmentResponse> myTeams(AuthenticatedUser mentor) {
        return assignmentRepository.findAllByMentor_Id(mentor.id()).stream()
                .map(MentoringMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<MentorCallResponse> myCalls(AuthenticatedUser mentor) {
        return callRepository.findAllByMentor_IdOrderByStartsAtAsc(mentor.id()).stream()
                .map(MentoringMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<MentorCommentResponse> comments(AuthenticatedUser user, Long stageSubmissionId) {
        var submission = submissionReaderService.getSubmissionById(stageSubmissionId);
        var project = submissionReaderService.getProjectById(submission.getProjectId());
        if (project.getTeam() == null) {
            throw new ForbiddenException("Mentor comments are available only for team submissions");
        }

        var canRead = assignmentRepository.existsByTeam_IdAndMentor_Id(project.getTeam().getId(), user.id())
                || teamService.isMember(project.getTeam().getId(), user.id());
        if (!canRead) {
            throw new ForbiddenException("No access to mentor comments");
        }

        return commentRepository.findAllByStageSubmission_IdOrderByCreatedAtDesc(stageSubmissionId).stream()
                .map(comment -> new MentorCommentResponse(
                        comment.getId(),
                        comment.getStageSubmission().getId(),
                        comment.getMentor().getId(),
                        comment.getText(),
                        comment.getCreatedAt()
                ))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<MentorCallResponse> teamCalls(AuthenticatedUser user, Long teamId) {
        var canRead = assignmentRepository.existsByTeam_IdAndMentor_Id(teamId, user.id())
                || teamService.isMember(teamId, user.id());
        if (!canRead) {
            throw new ForbiddenException("No access to mentor calls");
        }

        return callRepository.findAllByTeam_IdOrderByStartsAtAsc(teamId).stream()
                .map(MentoringMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public com.launchgate.submission.dto.StageSubmissionResponse stageSubmission(AuthenticatedUser user, Long stageSubmissionId) {
        var submission = submissionReaderService.getSubmissionById(stageSubmissionId);
        var project = submissionReaderService.getProjectById(submission.getProjectId());
        if (project.getTeam() == null) {
            throw new ForbiddenException("Mentor can open only team submissions");
        }

        var canRead = assignmentRepository.existsByTeam_IdAndMentor_Id(project.getTeam().getId(), user.id())
                || teamService.isMember(project.getTeam().getId(), user.id());
        if (!canRead) {
            throw new ForbiddenException("No access to stage submission");
        }

        return submissionReaderService.getSubmissionResponse(stageSubmissionId);
    }

    @Transactional
    public Long comment(AuthenticatedUser mentor, MentorCommentRequest request) {
        var submission = submissionReaderService.getSubmissionById(request.stageSubmissionId());
        var project = submissionReaderService.getProjectById(submission.getProjectId());
        if (project.getTeam() == null || !assignmentRepository.existsByTeam_IdAndMentor_Id(project.getTeam().getId(), mentor.id())) {
            throw new ForbiddenException("Mentor is not assigned to this team submission");
        }

        var mentorUser = userService.getUserById(mentor.id());
        return commentRepository.save(new MentorComment(
                submission,
                mentorUser,
                request.text(),
                Instant.now(clock)
        )).getId();
    }

    @Transactional
    public Long scheduleCall(AuthenticatedUser mentor, ScheduleCallRequest request) {
        if (!assignmentRepository.existsByTeam_IdAndMentor_Id(request.teamId(), mentor.id())) {
            throw new ForbiddenException("Mentor is not assigned to this team");
        }
        if (!request.endsAt().isAfter(request.startsAt())) {
            throw new DomainException("bad_call_time", "Call end time must be after start time");
        }

        var team = teamService.getTeam(request.teamId());
        var mentorUser = userService.getUserById(mentor.id());
        return callRepository.save(new MentorCall(
                team,
                mentorUser,
                request.startsAt(),
                request.endsAt(),
                request.link(),
                request.notes()
        )).getId();
    }
}
