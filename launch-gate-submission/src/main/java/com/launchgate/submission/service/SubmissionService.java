package com.launchgate.submission.service;

import com.launchgate.common.DomainException;
import com.launchgate.common.ForbiddenException;
import com.launchgate.common.NotFoundException;
import com.launchgate.contest.entity.ContestStatus;
import com.launchgate.contest.entity.ContestRole;
import com.launchgate.contest.entity.ParticipationMode;
import com.launchgate.contest.entity.SubmissionField;
import com.launchgate.contest.entity.stage.ContestStage;
import com.launchgate.contest.service.ContestReaderService;
import com.launchgate.contest.service.ContestRolePolicy;
import com.launchgate.contest.service.TeamService;
import com.launchgate.contest.utils.stage.StageMapper;
import com.launchgate.identity.dto.AuthenticatedUser;
import com.launchgate.identity.service.UserService;
import com.launchgate.submission.dto.MyProjectsResponse;
import com.launchgate.submission.dto.ProjectRequest;
import com.launchgate.submission.dto.ProjectResponse;
import com.launchgate.submission.dto.StageSubmissionResponse;
import com.launchgate.submission.dto.ValueRequest;
import com.launchgate.submission.entity.Project;
import com.launchgate.submission.entity.StageSubmission;
import com.launchgate.submission.entity.SubmissionStatus;
import com.launchgate.submission.entity.SubmissionValue;
import com.launchgate.submission.repository.ProjectRepository;
import com.launchgate.submission.repository.StageSubmissionRepository;
import com.launchgate.submission.repository.SubmissionValueRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
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
    private final SubmissionValueValidationService submissionValueValidationService;
    private final UserService userService;
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
            var team = teamService.getTeam(request.teamId());
            var project = projectRepository.findByContest_IdAndTeam_Id(request.contestId(), request.teamId())
                    .orElseGet(() -> projectRepository.save(new Project(
                            contest,
                            team,
                            null,
                            Instant.now(clock)
                    )));
            return projectResponse(project);
        }

        var owner = userService.getUserById(user.id());
        var project = projectRepository.findByContest_IdAndOwnerParticipant_Id(request.contestId(), user.id())
                .orElseGet(() -> projectRepository.save(new Project(
                        contest,
                        null,
                        owner,
                        Instant.now(clock)
                )));
        return projectResponse(project);
    }

    @Transactional
    public StageSubmissionResponse saveValue(AuthenticatedUser user, Long projectId, Long stageId, ValueRequest request) {
        var project = requireEditableProject(user, projectId);
        var stage = contestReaderService.getStageById(stageId);
        if (!stage.getContestId().equals(project.getContest().getId())) {
            throw new DomainException("stage_mismatch", "Stage does not belong to project contest");
        }

        var submission = submissionRepository.findByProject_IdAndStage_Id(projectId, stageId)
                .orElseGet(() -> submissionRepository.save(new StageSubmission(project, stage, Instant.now(clock))));
        if (submission.getStatus() == SubmissionStatus.SUBMITTED) {
            throw new DomainException("submission_locked", "Submitted stage cannot be edited");
        }

        var field = contestReaderService.getSubmissionFields(stageId).stream()
                .filter(candidate -> candidate.getId().equals(request.fieldId()))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Submission field not found"));

        var value = valueRepository.findBySubmission_IdAndField_Id(submission.getId(), request.fieldId())
                .orElseGet(() -> new SubmissionValue(submission, field));
        var valueText = submissionValueValidationService.validateAndNormalizeValueText(field, request);
        var fileIds = submissionValueValidationService.validateAndNormalizeFileIds(field, request);
        value.update(valueText, fileIds);
        valueRepository.save(value);
        return submissionResponse(stage, submission);
    }

    @Transactional
    public StageSubmissionResponse submit(AuthenticatedUser user, Long projectId, Long stageId) {
        var project = requireEditableProject(user, projectId);
        if (project.getTeamId() != null && !teamService.isLeader(project.getTeamId(), user.id())) {
            throw new ForbiddenException("Only team leader can submit final stage data");
        }

        var submission = submissionRepository.findByProject_IdAndStage_Id(projectId, stageId)
                .orElseThrow(() -> new DomainException("empty_submission", "Create draft values before submitting"));
        validateRequiredFields(stageId, submission.getId());
        submission.submit(userService.getUserById(user.id()), Instant.now(clock));
        return submissionResponse(contestReaderService.getStageById(stageId), submission);
    }

    @Transactional(readOnly = true)
    public ProjectResponse project(AuthenticatedUser user, Long projectId) {
        var project = projectRepository.findById(projectId)
                .orElseThrow(() -> new NotFoundException("Project not found"));
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
        var individualProjects = projectRepository.findAllByOwnerParticipant_IdOrderByCreatedAtDesc(user.id());
        var teamIds = teamService.getTeamIdsByParticipant(user.id());
        var teamProjects = teamIds.isEmpty()
                ? List.<Project>of()
                : projectRepository.findAllByTeam_IdInOrderByCreatedAtDesc(teamIds);
        var projects = java.util.stream.Stream.concat(individualProjects.stream(), teamProjects.stream())
                .sorted(java.util.Comparator.comparing(Project::getCreatedAt).reversed())
                .toList();

        var activeProjects = projects.stream()
                .filter(project -> project.getContest().getStatus() != ContestStatus.FINISHED)
                .map(this::projectResponse)
                .toList();
        var archivedProjects = projects.stream()
                .filter(project -> project.getContest().getStatus() == ContestStatus.FINISHED)
                .map(this::projectResponse)
                .toList();
        return new MyProjectsResponse(activeProjects, archivedProjects);
    }

    private void validateRequiredFields(Long stageId, Long submissionId) {
        var filledFields = valueRepository.findAllBySubmission_Id(submissionId).stream()
                .filter(value -> (value.getValueText() != null && !value.getValueText().isBlank())
                        || (value.getFileIds() != null && !value.getFileIds().isBlank()))
                .map(value -> value.getField().getId())
                .collect(Collectors.toSet());
        var missing = contestReaderService.getSubmissionFields(stageId).stream()
                .filter(SubmissionField::isRequired)
                .filter(field -> !filledFields.contains(field.getId()))
                .map(SubmissionField::getTitle)
                .toList();
        if (!missing.isEmpty()) {
            throw new DomainException("required_fields_missing", "Missing required fields: " + String.join(", ", missing));
        }
    }

    private Project requireEditableProject(AuthenticatedUser user, Long projectId) {
        var project = projectRepository.findById(projectId)
                .orElseThrow(() -> new NotFoundException("Project not found"));
        if (!canReadProject(user, project)) {
            throw new ForbiddenException("No access to project");
        }
        return project;
    }

    private boolean canReadProject(AuthenticatedUser user, Project project) {
        if (project.getOwnerParticipant() != null) {
            return project.getOwnerParticipant().getId().equals(user.id());
        }
        return project.getTeam() != null && teamService.isMember(project.getTeam().getId(), user.id());
    }

    private ProjectResponse projectResponse(Project project) {
        var stages = contestReaderService.stages(project.getContest().getId()).stream()
                .map(stage -> submissionRepository.findByProject_IdAndStage_Id(project.getId(), stage.getId())
                        .map(submission -> submissionResponse(stage, submission))
                        .orElse(new StageSubmissionResponse(
                                null,
                                SubmissionStatus.DRAFT,
                                StageMapper.toStageParticipantResponse(stage),
                                List.of()
                        )))
                .toList();
        return SubmissionMapper.toProjectResponse(project, stages);
    }

    private StageSubmissionResponse submissionResponse(ContestStage stage, StageSubmission submission) {
        return SubmissionMapper.toSubmissionResponse(
                submission,
                StageMapper.toStageParticipantResponse(stage),
                valueRepository.findAllBySubmission_Id(submission.getId())
        );
    }
}
