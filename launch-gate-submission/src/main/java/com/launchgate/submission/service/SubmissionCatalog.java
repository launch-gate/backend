package com.launchgate.submission.service;

import lombok.RequiredArgsConstructor;

import com.launchgate.submission.dto.*;
import com.launchgate.submission.entity.*;
import com.launchgate.submission.repository.*;

import com.launchgate.common.NotFoundException;
import com.launchgate.contest.utils.stage.StageMapper;
import com.launchgate.contest.service.ContestReaderService;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SubmissionCatalog {
    private final ProjectRepository projectRepository;
    private final StageSubmissionRepository submissionRepository;
    private final SubmissionValueRepository valueRepository;
    private final ContestReaderService contestReaderService;
    @Transactional(readOnly = true)
    public StageSubmission requireSubmission(Long submissionId) {
        return submissionRepository.findById(submissionId).orElseThrow(() -> new NotFoundException("Submission not found"));
    }

    @Transactional(readOnly = true)
    public Project requireProject(Long projectId) {
        return projectRepository.findById(projectId).orElseThrow(() -> new NotFoundException("Project not found"));
    }

    @Transactional(readOnly = true)
    public List<SubmissionValue> values(Long submissionId) {
        return valueRepository.findAllBySubmissionId(submissionId);
    }

    @Transactional(readOnly = true)
    public StageSubmissionResponse submissionResponse(Long submissionId) {
        var submission = requireSubmission(submissionId);
        var stage = contestReaderService.getStageById(submission.getStageId());
        return SubmissionMapper.toSubmissionResponse(
                submission,
                StageMapper.toStageParticipantResponse(stage),
                valueRepository.findAllBySubmissionId(submissionId)
        );
    }

    @Transactional(readOnly = true)
    public List<SubmissionSummary> submittedByStage(Long stageId) {
        return submissionRepository.findAllByStageId(stageId).stream()
                .filter(submission -> submission.getStatus() == SubmissionStatus.SUBMITTED)
                .map(submission -> {
                    var project = requireProject(submission.getProjectId());
                    var solutionTitle = valueRepository.findAllBySubmissionId(submission.getId()).stream()
                            .filter(value -> contestReaderService.fields(submission.getStageId()).stream()
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
    public long submittedCount(Long stageId) {
        return submissionRepository.countByStageIdAndStatus(stageId, SubmissionStatus.SUBMITTED);
    }
}
