package com.launchgate.contest.service;

import com.launchgate.contest.entity.stage.ContestStage;
import lombok.RequiredArgsConstructor;

import com.launchgate.contest.dto.*;
import com.launchgate.contest.entity.*;
import com.launchgate.contest.repository.*;

import com.launchgate.common.NotFoundException;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ContestReaderService {
    private final ContestRepository contestRepository;
    private final ContestStageRepository stageRepository;
    private final SubmissionFieldRepository fieldRepository;
    private final ContestResourceRepository resourceRepository;
    private final ContestRegistrationRepository registrationRepository;
    private final TeamRepository teamRepository;

    public Contest getContestById(Long contestId) {
        return contestRepository.findById(contestId).orElseThrow(() -> new NotFoundException("Contest not found"));
    }

    public ContestStage getStageById(Long stageId) {
        return stageRepository.findById(stageId).orElseThrow(() -> new NotFoundException("Stage not found"));
    }

    public List<ContestStage> stages(Long contestId) {
        return stageRepository.findAllByContestIdOrderByOrderAsc(contestId);
    }

    public List<SubmissionField> fields(Long stageId) {
        return fieldRepository.findAllByStageIdOrderByOrderAsc(stageId);
    }

    public List<ContestResource> getStageResources(Long stageId) {
        return resourceRepository.findAllByStageIdOrderByOrderAsc(stageId);
    }

    public ContestMetrics metrics(Long contestId) {
        return new ContestMetrics(
                registrationRepository.countByContestId(contestId),
                teamRepository.countByContestId(contestId),
                stageRepository.countByContestId(contestId)
        );
    }
}
