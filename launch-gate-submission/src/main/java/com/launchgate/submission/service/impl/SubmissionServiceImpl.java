package com.launchgate.submission.service.impl;

import com.launchgate.common.ForbiddenException;
import com.launchgate.common.LaunchGateException;
import com.launchgate.common.NotFoundException;
import com.launchgate.contest.entity.ContestRole;
import com.launchgate.contest.entity.SubmissionField;
import com.launchgate.contest.entity.stage.ContestStage;
import com.launchgate.contest.service.ContestReaderService;
import com.launchgate.contest.service.ContestRolePolicy;
import com.launchgate.contest.service.TeamService;
import com.launchgate.identity.dto.AuthenticatedUser;
import com.launchgate.identity.service.UserService;
import com.launchgate.submission.dto.StageSubmissionResponse;
import com.launchgate.submission.dto.ValueRequest;
import com.launchgate.submission.entity.Project;
import com.launchgate.submission.entity.StageSubmission;
import com.launchgate.submission.entity.SubmissionStatus;
import com.launchgate.submission.entity.SubmissionValue;
import com.launchgate.submission.repository.StageSubmissionRepository;
import com.launchgate.submission.repository.SubmissionValueRepository;
import com.launchgate.submission.service.ProjectResponseAssembler;
import com.launchgate.submission.service.ProjectValidationService;
import com.launchgate.submission.service.SubmissionService;
import com.launchgate.submission.service.SubmissionValueValidationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

/**
 * Реализация сервиса управления отправками и черновиками форм этапов.
 */
@Service
@RequiredArgsConstructor
public class SubmissionServiceImpl implements SubmissionService {
    private final StageSubmissionRepository submissionRepository;
    private final SubmissionValueRepository valueRepository;
    private final ContestReaderService contestReaderService;
    private final ContestRolePolicy rolePolicy;
    private final TeamService teamService;
    private final SubmissionValueValidationService submissionValueValidationService;
    private final UserService userService;
    private final ProjectValidationService projectValidationService;
    private final ProjectResponseAssembler projectResponseAssembler;
    private final Clock clock;

    @Override
    @Transactional
    public StageSubmissionResponse saveValue(AuthenticatedUser user, Long projectId, Long stageId, ValueRequest request) {
        Project project = projectValidationService.requireEditableProject(user, projectId);
        ContestStage stage = contestReaderService.getStageById(stageId);

        if (!stage.getContestId().equals(project.getContest().getId())) {
            throw new LaunchGateException("Выбранный этап относится к другому конкурсу");
        }

        StageSubmission submission = submissionRepository.findByProject_IdAndStage_Id(projectId, stageId)
                .orElseGet(() -> submissionRepository.save(new StageSubmission(project, stage, Instant.now(clock))));

        if (submission.getStatus() == SubmissionStatus.SUBMITTED) {
            throw new LaunchGateException("Отправленный на проверку этап нельзя редактировать");
        }

        SubmissionField field = contestReaderService.getSubmissionFields(stageId).stream()
                .filter(candidate -> candidate.getId().equals(request.fieldId()))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Этап не найден"));

        SubmissionValue value = valueRepository.findBySubmission_IdAndField_Id(submission.getId(), request.fieldId())
                .orElseGet(() -> new SubmissionValue(submission, field));

        String valueText = submissionValueValidationService.validateAndNormalizeValueText(field, request);
        String fileIds = submissionValueValidationService.validateAndNormalizeFileIds(field, request);

        value.update(valueText, fileIds);
        valueRepository.save(value);
        return projectResponseAssembler.createSubmissionResponse(stage, submission);
    }

    @Override
    @Transactional
    public StageSubmissionResponse submit(AuthenticatedUser user, Long projectId, Long stageId) {
        Project project = projectValidationService.requireEditableProject(user, projectId);

        if (Objects.nonNull(project.getTeamId()) && !teamService.isLeader(project.getTeamId(), user.id())) {
            throw new ForbiddenException("Только лидер команды может отправить финальные данные этапа");
        }

        StageSubmission submission = submissionRepository.findByProject_IdAndStage_Id(projectId, stageId)
                .orElseThrow(() -> new LaunchGateException("Создайте черновик перед отправкой"));

        submissionValueValidationService.validateRequiredFields(stageId, submission.getId());
        submission.submit(userService.getUserById(user.id()), Instant.now(clock));
        return projectResponseAssembler.createSubmissionResponse(contestReaderService.getStageById(stageId), submission);
    }

    @Override
    @Transactional(readOnly = true)
    public StageSubmissionResponse organizerSubmission(AuthenticatedUser user, Long submissionId) {
        StageSubmission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new NotFoundException("Отправленное решение не найдено"));

        ContestStage stage = contestReaderService.getStageById(submission.getStageId());
        rolePolicy.requireAny(stage.getContestId(), user.id(), ContestRole.CREATOR, ContestRole.ADMIN);
        return projectResponseAssembler.createSubmissionResponse(stage, submission);
    }
}

