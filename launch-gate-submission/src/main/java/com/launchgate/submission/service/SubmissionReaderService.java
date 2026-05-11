package com.launchgate.submission.service;

import com.launchgate.common.NotFoundException;
import com.launchgate.contest.service.ContestReaderService;
import com.launchgate.contest.utils.stage.StageMapper;
import com.launchgate.submission.dto.StageSubmissionResponse;
import com.launchgate.submission.dto.SubmissionSummary;
import com.launchgate.submission.entity.Project;
import com.launchgate.submission.entity.StageSubmission;
import com.launchgate.submission.entity.SubmissionStatus;
import com.launchgate.submission.entity.SubmissionValue;
import com.launchgate.submission.repository.ProjectRepository;
import com.launchgate.submission.repository.StageSubmissionRepository;
import com.launchgate.submission.repository.SubmissionValueRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SubmissionReaderService {
    private final ProjectRepository projectRepository;
    private final StageSubmissionRepository submissionRepository;
    private final SubmissionValueRepository valueRepository;
    private final ContestReaderService contestReaderService;

    @Transactional(readOnly = true)
    public StageSubmission getSubmissionById(Long submissionId) {
        return submissionRepository.findById(submissionId)
                .orElseThrow(() -> new NotFoundException("Submission not found"));
    }

    @Transactional(readOnly = true)
    public Project getProjectById(Long projectId) {
        return projectRepository.findById(projectId)
                .orElseThrow(() -> new NotFoundException("Project not found"));
    }

    @Transactional(readOnly = true)
    public List<SubmissionValue> getSubmissionValues(Long submissionId) {
        return valueRepository.findAllBySubmission_Id(submissionId);
    }

    @Transactional(readOnly = true)
    public StageSubmissionResponse getSubmissionResponse(Long submissionId) {
        var submission = getSubmissionById(submissionId);
        var stage = contestReaderService.getStageById(submission.getStageId());
        return SubmissionMapper.toSubmissionResponse(
                submission,
                StageMapper.toStageParticipantResponse(stage),
                getSubmissionValues(submissionId)
        );
    }

    @Transactional(readOnly = true)
    public List<SubmissionSummary> getSubmittedSubmissionsByStage(Long stageId) {
        return submissionRepository.findAllByStage_Id(stageId).stream()
                .filter(submission -> submission.getStatus() == SubmissionStatus.SUBMITTED)
                .map(submission -> {
                    var project = getProjectById(submission.getProjectId());
                    var stageFields = contestReaderService.getSubmissionFields(stageId);
                    var solutionTitle = getSubmissionValues(submission.getId()).stream()
                            .filter(value -> stageFields.stream()
                                    .anyMatch(field -> field.getId().equals(value.getFieldId()) && "Project name".equals(field.getTitle())))
                            .map(SubmissionValue::getValueText)
                            .filter(value -> value != null && !value.isBlank())
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
                })
                .toList();
    }

    @Transactional(readOnly = true)
    public long getSubmittedSubmissionCountByStage(Long stageId) {
        return submissionRepository.countByStage_IdAndStatus(stageId, SubmissionStatus.SUBMITTED);
    }
}
