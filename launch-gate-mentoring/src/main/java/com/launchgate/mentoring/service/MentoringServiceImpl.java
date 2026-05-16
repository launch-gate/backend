package com.launchgate.mentoring.service;

import com.launchgate.common.ForbiddenException;
import com.launchgate.common.LaunchGateException;
import com.launchgate.contest.entity.ContestRole;
import com.launchgate.contest.entity.team.Team;
import com.launchgate.contest.service.ContestRolePolicy;
import com.launchgate.contest.service.TeamService;
import com.launchgate.identity.dto.AuthenticatedUser;
import com.launchgate.identity.entity.UserAccount;
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
import com.launchgate.mentoring.mapper.MentoringMapper;
import com.launchgate.mentoring.repository.MentorAssignmentRepository;
import com.launchgate.mentoring.repository.MentorCallRepository;
import com.launchgate.mentoring.repository.MentorCommentRepository;
import com.launchgate.submission.dto.StageSubmissionResponse;
import com.launchgate.submission.entity.Project;
import com.launchgate.submission.entity.StageSubmission;
import com.launchgate.submission.service.SubmissionReaderService;
import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Objects;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Реализация сервис для взаимодействия с менторами.
 */
@Service
@RequiredArgsConstructor
public class MentoringServiceImpl implements MentoringService {
    private final MentorAssignmentRepository assignmentRepository;
    private final MentorCommentRepository commentRepository;
    private final MentorCallRepository callRepository;
    private final TeamService teamService;
    private final ContestRolePolicy rolePolicy;
    private final UserService userService;
    private final SubmissionReaderService submissionReaderService;
    private final Clock clock;

    @Override
    @Transactional
    public MentorAssignmentResponse assign(AuthenticatedUser organizer, AssignMentorRequest request) {

        Team team = teamService.getTeam(request.teamId());
        rolePolicy.requireAny(team.getContestId(), organizer.id(), ContestRole.CREATOR, ContestRole.ADMIN);
        UserAccount mentor = userService.getUserById(request.mentorUserId());

        if (!rolePolicy.hasAny(team.getContestId(), mentor.getId(), ContestRole.MENTOR)) {
            throw new ForbiddenException("Пользователь должен иметь роль ментора в конкурсе");
        }

        MentorAssignment assignment = assignmentRepository.findByTeamIdAndMentorId(team.getId(), mentor.getId())
                .orElseGet(() -> assignmentRepository.save(new MentorAssignment(
                        team.getContest(),
                        team,
                        mentor,
                        Instant.now(clock)
                )));

        return MentoringMapper.toResponse(assignment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MentorAssignmentResponse> myTeams(AuthenticatedUser mentor) {
        return assignmentRepository.findAllByMentorId(mentor.id()).stream()
                .map(MentoringMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<MentorCallResponse> myCalls(AuthenticatedUser mentor) {
        return callRepository.findAllByMentorIdOrderByStartsAtAsc(mentor.id()).stream()
                .map(MentoringMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<MentorCommentResponse> comments(AuthenticatedUser user, Long stageSubmissionId) {

        StageSubmission submission = submissionReaderService.getSubmissionById(stageSubmissionId);
        Project project = submissionReaderService.getProjectById(submission.getProjectId());

        if (Objects.isNull(project.getTeam())) {
            throw new ForbiddenException("Комментарии ментора доступны только для решений команд");
        }

        boolean canRead = assignmentRepository.existsByTeamIdAndMentorId(project.getTeam().getId(), user.id())
                || teamService.isMember(project.getTeam().getId(), user.id());

        if (!canRead) {
            throw new ForbiddenException("Нет доступа к комментариям ментора");
        }

        return commentRepository.findAllByStageSubmissionIdOrderByCreatedAtDesc(stageSubmissionId).stream()
                .map(comment -> new MentorCommentResponse(
                        comment.getId(),
                        comment.getStageSubmission().getId(),
                        comment.getMentor().getId(),
                        comment.getText(),
                        comment.getCreatedAt()
                ))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<MentorCallResponse> teamCalls(AuthenticatedUser user, Long teamId) {
        boolean canRead = assignmentRepository.existsByTeamIdAndMentorId(teamId, user.id())
                || teamService.isMember(teamId, user.id());

        if (!canRead) {
            throw new ForbiddenException("Нет доступа к созвонам с ментором");
        }

        return callRepository.findAllByTeamIdOrderByStartsAtAsc(teamId).stream()
                .map(MentoringMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public StageSubmissionResponse stageSubmission(AuthenticatedUser user, Long stageSubmissionId) {

        StageSubmission submission = submissionReaderService.getSubmissionById(stageSubmissionId);
        Project project = submissionReaderService.getProjectById(submission.getProjectId());

        if (Objects.isNull(project.getTeam())) {
            throw new ForbiddenException("Ментор может открывать только решения команд");
        }

        boolean canRead = assignmentRepository.existsByTeamIdAndMentorId(project.getTeam().getId(), user.id())
                || teamService.isMember(project.getTeam().getId(), user.id());

        if (!canRead) {
            throw new ForbiddenException("Нет доступа к отправленному решению этапа");
        }

        return submissionReaderService.getSubmissionResponse(stageSubmissionId);
    }

    @Override
    @Transactional
    public Long comment(AuthenticatedUser mentor, MentorCommentRequest request) {

        StageSubmission submission = submissionReaderService.getSubmissionById(request.stageSubmissionId());
        Project project = submissionReaderService.getProjectById(submission.getProjectId());

        if (Objects.isNull(project.getTeam()) ||
                !assignmentRepository.existsByTeamIdAndMentorId(project.getTeam().getId(), mentor.id())) {
            throw new ForbiddenException("Ментор не назначен к этой команды");
        }

        var mentorUser = userService.getUserById(mentor.id());

        return commentRepository.save(new MentorComment(
                submission,
                mentorUser,
                request.text(),
                Instant.now(clock)
        )).getId();
    }

    @Override
    @Transactional
    public Long scheduleCall(AuthenticatedUser mentor, ScheduleCallRequest request) {

        if (!assignmentRepository.existsByTeamIdAndMentorId(request.teamId(), mentor.id())) {
            throw new ForbiddenException("Ментор не назначен на эту команду");
        }

        if (!request.endsAt().isAfter(request.startsAt())) {
            throw new LaunchGateException("Время окончания созвона должно быть позже времени начала");
        }

        Team team = teamService.getTeam(request.teamId());
        UserAccount mentorUser = userService.getUserById(mentor.id());

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
