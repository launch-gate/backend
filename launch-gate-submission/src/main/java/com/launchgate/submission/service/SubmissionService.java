package com.launchgate.submission.service;

import com.launchgate.contest.service.TeamService;
import lombok.RequiredArgsConstructor;

import com.launchgate.submission.dto.*;
import com.launchgate.submission.entity.*;
import com.launchgate.submission.repository.*;

import com.launchgate.common.DomainException;
import com.launchgate.common.ForbiddenException;
import com.launchgate.common.NotFoundException;
import com.launchgate.contest.entity.ContestStatus;
import com.launchgate.contest.entity.ContestRole;
import com.launchgate.contest.entity.stage.ContestStage;
import com.launchgate.contest.service.ContestReaderService;
import com.launchgate.contest.service.ContestRolePolicy;
import com.launchgate.contest.entity.ParticipationMode;
import com.launchgate.contest.entity.SubmissionField;
import com.launchgate.contest.utils.stage.StageMapper;
import com.launchgate.identity.dto.AuthenticatedUser;
import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SubmissionService {
    private final ProjectRepository projectRepository;
    private final StageSubmissionRepository submissionRepository;
    private final SubmissionValueRepository valueRepository;
    private final ContestReaderService contestReaderService;
    private final ContestRolePolicy rolePolicy;
    private final TeamService teamService;
    private final Clock clock;
    @Transactional
    public ProjectResponse createProject(AuthenticatedUser user, ProjectRequest request) {
        var contest = contestReaderService.getContestById(request.contestId());
        if (contest.getParticipationMode() == ParticipationMode.TEAM) {
            if (request.teamId() == null) {
                throw new DomainException("team_required", "Team contest project requires teamId");
            }
            if (!teamService.isMember(request.teamId(), user.id())) {
                throw new ForbiddenException("Only team member can create team project");
            }
            var project = projectRepository.findByContestIdAndTeamId(request.contestId(), request.teamId())
                    .orElseGet(() -> projectRepository.save(new Project(
                            request.contestId(),
                            request.teamId(),
                            null,
                            Instant.now(clock)
                    )));
            return projectResponse(project);
        }
        var project = projectRepository.findByContestIdAndOwnerParticipantId(request.contestId(), user.id())
                .orElseGet(() -> projectRepository.save(new Project(
                        request.contestId(),
                        null,
                        user.id(),
                        Instant.now(clock)
                )));
        return projectResponse(project);
    }

    @Transactional
    public StageSubmissionResponse saveValue(AuthenticatedUser user, Long projectId, Long stageId, ValueRequest request) {
        var project = requireEditableProject(user, projectId);
        var stage = contestReaderService.getStageById(stageId);
        if (!stage.getContestId().equals(project.getContestId())) {
            throw new DomainException("stage_mismatch", "Stage does not belong to project contest");
        }
        var submission = submissionRepository.findByProjectIdAndStageId(projectId, stageId)
                .orElseGet(() -> submissionRepository.save(new StageSubmission(projectId, stageId, Instant.now(clock))));
        if (submission.getStatus() == SubmissionStatus.SUBMITTED) {
            throw new DomainException("submission_locked", "Submitted stage cannot be edited");
        }
        var fieldExists = contestReaderService.fields(stageId).stream().anyMatch(field -> field.getId().equals(request.fieldId()));
        if (!fieldExists) {
            throw new NotFoundException("Submission field not found");
        }
        var value = valueRepository.findBySubmissionIdAndFieldId(submission.getId(), request.fieldId())
                .orElseGet(() -> new SubmissionValue(submission.getId(), request.fieldId()));
        value.update(request.valueText(), request.fileIds());
        valueRepository.save(value);
        return submissionResponse(stage, submission);
    }

    @Transactional
    public StageSubmissionResponse submit(AuthenticatedUser user, Long projectId, Long stageId) {
        var project = requireEditableProject(user, projectId);
        if (project.getTeamId() != null && !teamService.isLeader(project.getTeamId(), user.id())) {
            throw new ForbiddenException("Only team leader can submit final stage data");
        }
        var submission = submissionRepository.findByProjectIdAndStageId(projectId, stageId)
                .orElseThrow(() -> new DomainException("empty_submission", "Create draft values before submitting"));
        validateRequiredFields(stageId, submission.getId());
        submission.submit(user.id(), Instant.now(clock));
        return submissionResponse(contestReaderService.getStageById(stageId), submission);
    }

    @Transactional(readOnly = true)
    public ProjectResponse project(AuthenticatedUser user, Long projectId) {
        var project = projectRepository.findById(projectId).orElseThrow(() -> new NotFoundException("Project not found"));
        if (!canReadProject(user, project)) {
            throw new ForbiddenException("No access to project");
        }
        return projectResponse(project);
    }

    @Transactional(readOnly = true)
    public StageSubmissionResponse organizerSubmission(AuthenticatedUser user, Long submissionId) {
        var submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new NotFoundException("Submission not found"));
        var stage = contestReaderService.getStageById(submission.getStageId());
        rolePolicy.requireAny(stage.getContestId(), user.id(), ContestRole.CREATOR, ContestRole.ADMIN);
        return submissionResponse(stage, submission);
    }

    @Transactional(readOnly = true)
    public MyProjectsResponse myProjects(AuthenticatedUser user) {
        var individualProjects = projectRepository.findAllByOwnerParticipantIdOrderByCreatedAtDesc(user.id());
        var teamIds = teamService.getTeamIdsByParticipant(user.id());
        var teamProjects = teamIds.isEmpty()
                ? List.<Project>of()
                : projectRepository.findAllByTeamIdInOrderByCreatedAtDesc(teamIds);
        var projects = java.util.stream.Stream.concat(individualProjects.stream(), teamProjects.stream())
                .sorted(java.util.Comparator.comparing(Project::getCreatedAt).reversed())
                .toList();
        var activeProjects = projects.stream()
                .filter(project -> contestReaderService.getContestById(project.getContestId()).getStatus() != ContestStatus.FINISHED)
                .map(this::projectResponse)
                .toList();
        var archivedProjects = projects.stream()
                .filter(project -> contestReaderService.getContestById(project.getContestId()).getStatus() == ContestStatus.FINISHED)
                .map(this::projectResponse)
                .toList();
        return new MyProjectsResponse(activeProjects, archivedProjects);
    }

    private void validateRequiredFields(Long stageId, Long submissionId) {
        var filledFields = valueRepository.findAllBySubmissionId(submissionId).stream()
                .filter(value -> (value.getValueText() != null && !value.getValueText().isBlank())
                        || (value.getFileIds() != null && !value.getFileIds().isBlank()))
                .map(SubmissionValue::getFieldId)
                .collect(Collectors.toSet());
        var missing = contestReaderService.fields(stageId).stream()
                .filter(SubmissionField::isRequired)
                .filter(field -> !filledFields.contains(field.getId()))
                .map(SubmissionField::getTitle)
                .toList();
        if (!missing.isEmpty()) {
            throw new DomainException("required_fields_missing", "Missing required fields: " + String.join(", ", missing));
        }
    }

    private Project requireEditableProject(AuthenticatedUser user, Long projectId) {
        var project = projectRepository.findById(projectId).orElseThrow(() -> new NotFoundException("Project not found"));
        if (!canReadProject(user, project)) {
            throw new ForbiddenException("No access to project");
        }
        return project;
    }

    private boolean canReadProject(AuthenticatedUser user, Project project) {
        if (project.getOwnerParticipantId() != null) {
            return project.getOwnerParticipantId().equals(user.id());
        }
        return project.getTeamId() != null && teamService.isMember(project.getTeamId(), user.id());
    }

    private ProjectResponse projectResponse(Project project) {
        var stages = contestReaderService.stages(project.getContestId()).stream()
                .map(stage -> submissionRepository.findByProjectIdAndStageId(project.getId(), stage.getId())
                        .map(submission -> submissionResponse(stage, submission))
                        .orElse(new StageSubmissionResponse(null, SubmissionStatus.DRAFT, StageMapper.toStageParticipantResponse(stage), List.of())))
                .toList();
        return SubmissionMapper.toProjectResponse(project, stages);
    }

    private StageSubmissionResponse submissionResponse(ContestStage stage, StageSubmission submission) {
        return SubmissionMapper.toSubmissionResponse(
                submission,
                StageMapper.toStageParticipantResponse(stage),
                valueRepository.findAllBySubmissionId(submission.getId())
        );
    }
}
