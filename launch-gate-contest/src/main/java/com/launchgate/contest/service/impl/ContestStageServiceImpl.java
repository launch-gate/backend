package com.launchgate.contest.service.impl;

import com.launchgate.common.NotFoundException;
import com.launchgate.contest.dto.stage.StageOrganizesResponse;
import com.launchgate.contest.dto.stage.StageParticipantResponse;
import com.launchgate.contest.dto.stage.StageRequest;
import com.launchgate.contest.entity.Contest;
import com.launchgate.contest.entity.stage.ContestStage;
import com.launchgate.contest.enums.ContestRole;
import com.launchgate.contest.enums.ContestStatus;
import com.launchgate.contest.repository.ContestStageRepository;
import com.launchgate.contest.service.ContestReaderService;
import com.launchgate.contest.service.ContestRolePolicy;
import com.launchgate.contest.service.api.ContestStageService;
import com.launchgate.contest.utils.SortOrderService;
import com.launchgate.contest.utils.stage.StageMapper;
import com.launchgate.identity.dto.AuthenticatedUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Реализация {@link ContestStageService}.
 */
@Service
@RequiredArgsConstructor
public class ContestStageServiceImpl implements ContestStageService {
    private final ContestReaderService contestReaderService;
    private final ContestStageRepository stageRepository;
    private final ContestRolePolicy rolePolicy;

    @Override
    @Transactional
    public StageOrganizesResponse create(AuthenticatedUser user, Long contestId, StageRequest request) {
        rolePolicy.requireAny(contestId, user.id(), ContestRole.CREATOR, ContestRole.ADMIN);
        Contest contest = contestReaderService.getContestById(contestId);
        List<ContestStage> stages = stageRepository.findAllByContestIdOrderByOrderAsc(contestId);

        Integer targetOrder = SortOrderService.normalizeRequestedOrder(request.order(), stages.size());
        SortOrderService.moveOrder(stages, targetOrder);

        ContestStage stage = new ContestStage(
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

    @Override
    @Transactional
    public StageOrganizesResponse update(AuthenticatedUser user, Long stageId, StageRequest request) {
        ContestStage stage = contestReaderService.getStageById(stageId);
        rolePolicy.requireAny(stage.getContestId(), user.id(), ContestRole.CREATOR, ContestRole.ADMIN);

        List<ContestStage> stages = stageRepository.findAllByContestIdOrderByOrderAsc(stage.getContestId());
        SortOrderService.replaceOrder(stage, stages, request.order());

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

    @Override
    @Transactional
    public Long delete(AuthenticatedUser user, Long stageId) {
        ContestStage stage = contestReaderService.getStageById(stageId);
        Long contestId = stage.getContestId();
        rolePolicy.requireAny(contestId, user.id(), ContestRole.CREATOR, ContestRole.ADMIN);
        stageRepository.delete(stage);

        SortOrderService.reorder(stageRepository.findAllByContestIdOrderByOrderAsc(contestId));
        return stageId;
    }

    @Override
    @Transactional(readOnly = true)
    public List<StageOrganizesResponse> organizerStages(Long contestId) {
        List<ContestStage> stages = stageRepository.findAllByContestIdOrderByOrderAsc(contestId);
        return stages.stream()
                .map(StageMapper::toStageOrganizesResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public StageOrganizesResponse getOrganizerStage(Long stageId) {
        ContestStage stage = contestReaderService.getStageById(stageId);
        return StageMapper.toStageOrganizesResponse(stage);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StageParticipantResponse> publicStages(Long contestId) {
        Contest contest = contestReaderService.getContestById(contestId);
        requirePublicContest(contest);
        List<ContestStage> stages = stageRepository.findAllByContestIdOrderByOrderAsc(contestId);

        return stages.stream()
                .map(StageMapper::toStageParticipantResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public StageParticipantResponse publicStage(Long stageId) {
        ContestStage stage = contestReaderService.getStageById(stageId);
        requirePublicContest(stage.getContest());

        return StageMapper.toStageParticipantResponse(stage);
    }

    private void requirePublicContest(Contest contest) {
        if (contest == null || contest.getStatus() == ContestStatus.DRAFT) {
            throw new NotFoundException("Конкурс не найден");
        }
    }
}

