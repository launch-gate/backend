package com.launchgate.submission.service;

import com.launchgate.common.NotFoundException;
import com.launchgate.contest.entity.SubmissionField;
import com.launchgate.contest.entity.stage.ContestStage;
import com.launchgate.contest.service.ContestReaderService;
import com.launchgate.contest.utils.stage.StageMapper;
import com.launchgate.submission.dto.ProjectResponse;
import com.launchgate.submission.dto.StageSubmissionResponse;
import com.launchgate.submission.dto.SubmissionSummary;
import com.launchgate.submission.entity.Project;
import com.launchgate.submission.entity.StageSubmission;
import com.launchgate.submission.entity.SubmissionStatus;
import com.launchgate.submission.entity.SubmissionValue;
import com.launchgate.submission.mapper.SubmissionMapper;
import com.launchgate.submission.repository.ProjectRepository;
import com.launchgate.submission.repository.StageSubmissionRepository;
import com.launchgate.submission.repository.SubmissionValueRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Сервис формирования результатов проектов и отправленных форм.
 */
@Service
@RequiredArgsConstructor
public class ProjectResponseAssembler {

    private final StageSubmissionRepository submissionRepository;
    private final SubmissionValueRepository valueRepository;
    private final ContestReaderService contestReaderService;
    private final ProjectRepository projectRepository;

    /**
     * Формирует полное представление проекта со всеми этапами конкурса и их статусами.
     * @param project проект.
     * @return рабочее пространства проекта.
     */
    public ProjectResponse createProjectResponse(Project project) {
        List<StageSubmissionResponse> stages = contestReaderService.stages(project.getContest().getId()).stream()
                .map(stage -> submissionRepository.findByProject_IdAndStage_Id(project.getId(), stage.getId())
                        .map(submission -> createSubmissionResponse(stage, submission))
                        .orElse(new StageSubmissionResponse(
                                null,
                                SubmissionStatus.DRAFT,
                                StageMapper.toStageParticipantResponse(stage),
                                List.of()
                        )))
                .toList();

        return SubmissionMapper.toProjectResponse(project, stages);
    }

    /**
     * Формирует детальный ответ по конкретной отправленной форме этапа.
     * @param stage стадия
     * @param submission форма
     * @return форма этапа.
     */
    public StageSubmissionResponse createSubmissionResponse(ContestStage stage, StageSubmission submission) {
        return SubmissionMapper.toSubmissionResponse(
                submission,
                StageMapper.toStageParticipantResponse(stage),
                valueRepository.findAllBySubmission_Id(submission.getId())
        );
    }

    /**
     * Формирует краткую сводку по отправленной форме этапа.
     * @param submission форма.
     * @param stageId идентификатор стадии.
     * @return краткая сводку по отправленной форме этапа.
     */
    public SubmissionSummary createSubmissionSummary(StageSubmission submission, Long stageId) {
        Project project = projectRepository.findById(submission.getProjectId())
                .orElseThrow(() -> new NotFoundException("Проект не найден"));

        List<SubmissionField>  stageFields = contestReaderService.getSubmissionFields(stageId);

        String solutionTitle = valueRepository.findAllBySubmission_Id(submission.getId()).stream()
                .filter(value -> stageFields.stream()
                        .anyMatch(field -> field.getId().equals(value.getFieldId()) && "Project name".equals(field.getTitle())))
                .map(SubmissionValue::getValueText)
                .filter(StringUtils::isNotBlank)
                .findFirst()
                .orElse("Submission #" + submission.getId());

        return new SubmissionSummary(
                submission.getId(),
                project.getId(),
                submission.getStageId(),
                project.getContestId(),
                solutionTitle,
                submission.getStatus()
        );
    }
}
