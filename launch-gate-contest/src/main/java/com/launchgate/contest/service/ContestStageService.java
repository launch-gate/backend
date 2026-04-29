package com.launchgate.contest.service;

import com.launchgate.common.NotFoundException;
import com.launchgate.contest.dto.stage.StageOrganizesResponse;
import com.launchgate.contest.dto.stage.StageParticipantResponse;
import com.launchgate.contest.dto.stage.StageRequest;
import com.launchgate.contest.entity.*;
import com.launchgate.contest.entity.stage.ContestStage;
import com.launchgate.contest.repository.ContestStageRepository;
import com.launchgate.contest.utils.stage.StageMapper;
import com.launchgate.identity.dto.AuthenticatedUser;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ContestStageService {
    private final ContestReaderService contestReaderService;
    private final SortOrderService sortOrderService;
    private final ContestStageRepository stageRepository;
    private final ContestRolePolicy rolePolicy;

    @Transactional
    public StageOrganizesResponse create(AuthenticatedUser user, Long contestId, StageRequest request) {
        rolePolicy.requireAny(contestId, user.id(), ContestRole.CREATOR, ContestRole.ADMIN);
        var contest = contestReaderService.getContestById(contestId);
        var stages = stageRepository.findAllByContestIdOrderByOrderAsc(contestId);

        var targetOrder = sortOrderService.normalizeRequestedOrder(request.order(), stages.size());
        sortOrderService.moveOrder(stages, targetOrder);

        var stage = new ContestStage(
                contest,
                targetOrder,
                request.title(),
                request.description(),
                request.rules(),
                request.extraInfo(),
                request.deadlineAt(),
                request.eliminating(),
                request.scoreScale()
        );
        stage = stageRepository.save(stage);
        return StageMapper.toStageOrganizesResponse(stage);
    }

    @Transactional
    public StageOrganizesResponse update(AuthenticatedUser user, Long stageId, StageRequest request) {
        var stage = contestReaderService.getStageById(stageId);
        rolePolicy.requireAny(stage.getContestId(), user.id(), ContestRole.CREATOR, ContestRole.ADMIN);

        var stages = stageRepository.findAllByContestIdOrderByOrderAsc(stage.getContestId());
        sortOrderService.replaceOrder(stage, stages, request.order());

        stage.update(
                request.title(),
                request.description(),
                request.rules(),
                request.extraInfo(),
                request.deadlineAt(),
                request.eliminating(),
                request.scoreScale()
        );
        return StageMapper.toStageOrganizesResponse(stage);
    }

    @Transactional
    public Long delete(AuthenticatedUser user, Long stageId) {
        var stage = contestReaderService.getStageById(stageId);
        var contestId = stage.getContestId();
        rolePolicy.requireAny(contestId, user.id(), ContestRole.CREATOR, ContestRole.ADMIN);
        stageRepository.delete(stage);

        sortOrderService.reorder(stageRepository.findAllByContestIdOrderByOrderAsc(contestId));
        return stageId;
    }

    @Transactional(readOnly = true)
    public List<StageOrganizesResponse> organizerStages(Long contestId) {
        var stages = stageRepository.findAllByContestIdOrderByOrderAsc(contestId);
        return stages.stream()
                .map(StageMapper::toStageOrganizesResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public StageOrganizesResponse getOrganizerStage(Long stageId) {
        var stage = contestReaderService.getStageById(stageId);
        return StageMapper.toStageOrganizesResponse(stage);
    }

    @Transactional(readOnly = true)
    public List<StageParticipantResponse> publicStages(Long contestId) {
        var contest = contestReaderService.getContestById(contestId);
        requirePublicContest(contest);
        var stages = stageRepository.findAllByContestIdOrderByOrderAsc(contestId);
        return stages.stream()
                .map(StageMapper::toStageParticipantResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public StageParticipantResponse publicStage(Long stageId) {
        var stage = contestReaderService.getStageById(stageId);
        requirePublicContest(stage.getContest());

        return StageMapper.toStageParticipantResponse(stage);
    }

    private void requirePublicContest(Contest contest) {
        if (contest == null || contest.getStatus() == ContestStatus.DRAFT) {
            throw new NotFoundException("Contest not found");
        }
    }
}
