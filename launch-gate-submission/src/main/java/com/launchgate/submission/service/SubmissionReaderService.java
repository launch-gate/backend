package com.launchgate.submission.service;

import com.launchgate.common.NotFoundException;
import com.launchgate.contest.entity.stage.ContestStage;
import com.launchgate.contest.service.ContestReaderService;
import com.launchgate.contest.utils.stage.StageMapper;
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

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Сервис получения данных и статистики по отправленным формам этапов.
 */
@Service
@RequiredArgsConstructor
public class SubmissionReaderService {
    private final ProjectRepository projectRepository;
    private final StageSubmissionRepository submissionRepository;
    private final SubmissionValueRepository valueRepository;
    private final ContestReaderService contestReaderService;
    private final ProjectResponseAssembler projectResponseAssembler;

    /**
     * Получить форму по идентификатору.
     *
     * @param submissionId идентификатор формы.
     * @return форма.
     */
    @Transactional(readOnly = true)
    public StageSubmission getSubmissionById(Long submissionId) {
        return submissionRepository.findById(submissionId)
                .orElseThrow(() -> new NotFoundException("Submission not found"));
    }

    /**
     * Получить проект по идентификатору.
     *
     * @param projectId идентификатор проекта.
     * @return проект.
     */
    @Transactional(readOnly = true)
    public Project getProjectById(Long projectId) {
        return projectRepository.findById(projectId)
                .orElseThrow(() -> new NotFoundException("Project not found"));
    }

    /**
     * Получить поля формы.
     *
     * @param submissionId иденификатор формы.
     * @return поля формы.
     */
    @Transactional(readOnly = true)
    public List<SubmissionValue> getSubmissionValues(Long submissionId) {
        return valueRepository.findAllBySubmission_Id(submissionId);
    }

    /**
     * Получить форму этапа.
     *
     * @param submissionId идентификатор формы.
     * @return форма этапа.
     */
    @Transactional(readOnly = true)
    public StageSubmissionResponse getSubmissionResponse(Long submissionId) {
        StageSubmission submission = getSubmissionById(submissionId);
        ContestStage stage = contestReaderService.getStageById(submission.getStageId());

        return SubmissionMapper.toSubmissionResponse(
                submission,
                StageMapper.toStageParticipantResponse(stage),
                getSubmissionValues(submissionId)
        );
    }

    /**
     * Получить список отправленных форм на этап.
     *
     * @param stageId идентфикатор стадии.
     * @return список форм.
     */
    @Transactional(readOnly = true)
    public List<SubmissionSummary> getSubmittedSubmissionsByStage(Long stageId) {
        return submissionRepository.findAllByStage_Id(stageId).stream()
                .filter(submission -> submission.getStatus() == SubmissionStatus.SUBMITTED)
                .map(submission -> projectResponseAssembler.createSubmissionSummary(submission, stageId))
                .toList();
    }

    /**
     * Получить отправленные формы этапа.
     *
     * @param stageId идентификатор стадии.
     * @return отправленные формы этапа.
     */
    @Transactional(readOnly = true)
    public List<StageSubmission> getSubmittedStageSubmissions(Long stageId) {
        return submissionRepository.findAllByStage_Id(stageId).stream()
                .filter(submission -> submission.getStatus() == SubmissionStatus.SUBMITTED)
                .toList();
    }

    /**
     * Получить количество отправленных форм на этап.
     *
     * @param stageId идентфикатор стадии.
     * @return количество форм.
     */
    @Transactional(readOnly = true)
    public long getSubmittedSubmissionCountByStage(Long stageId) {
        return submissionRepository.countByStage_IdAndStatus(stageId, SubmissionStatus.SUBMITTED);
    }
}
