package com.launchgate.contest.service.impl;

import com.launchgate.common.LaunchGateException;
import com.launchgate.contest.dto.resources.ResourceRequest;
import com.launchgate.contest.dto.resources.ResourceResponse;
import com.launchgate.contest.entity.ContestResource;
import com.launchgate.contest.enums.ContestRole;
import com.launchgate.contest.entity.stage.ContestStage;
import com.launchgate.contest.repository.ContestResourceRepository;
import com.launchgate.contest.service.ContestReaderService;
import com.launchgate.contest.service.api.ContestResourceService;
import com.launchgate.contest.service.ContestRolePolicy;
import com.launchgate.contest.utils.SortOrderService;
import com.launchgate.contest.utils.resource.ResourceMapper;
import com.launchgate.identity.dto.AuthenticatedUser;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Реализация {@link ContestResourceService}.
 */
@Service
@RequiredArgsConstructor
public class ContestResourceServiceImpl implements ContestResourceService {
    private final ContestResourceRepository resourceRepository;
    private final ContestReaderService contestReaderService;
    private final ContestRolePolicy rolePolicy;

    @Override
    @Transactional
    public ResourceResponse createStageResource(AuthenticatedUser user, Long stageId, ResourceRequest request) {
        ContestStage stage = validateAccessAndGetStage(user, stageId);
        List<ContestResource> resources = contestReaderService.getStageResources(stageId);
        Integer targetOrder = SortOrderService.normalizeRequestedOrder(request.order(), resources.size());
        SortOrderService.moveOrder(resources, targetOrder);

        ContestResource resource = new ContestResource(stage, targetOrder);
        resource.update(targetOrder, request.type(), request.title(), request.description(), request.linkUrl(), request.fileId());
        return ResourceMapper.toResourceResponse(resourceRepository.save(resource));
    }

    @Override
    @Transactional
    public ResourceResponse updateStageResource(AuthenticatedUser user, Long stageId, Long resourceId, ResourceRequest request) {
        validateAccessAndGetStage(user, stageId);
        ContestResource resource = requireStageResource(stageId, resourceId);
        SortOrderService.replaceOrder(resource, contestReaderService.getStageResources(stageId), request.order());
        resource.update(resource.getOrder(), request.type(), request.title(), request.description(), request.linkUrl(), request.fileId());
        return ResourceMapper.toResourceResponse(resource);
    }

    @Override
    @Transactional
    public Long deleteStageResource(AuthenticatedUser user, Long stageId, Long resourceId) {
        validateAccessAndGetStage(user, stageId);
        ContestResource resource = requireStageResource(stageId, resourceId);
        resourceRepository.delete(resource);
        SortOrderService.reorder(contestReaderService.getStageResources(stageId));
        return resourceId;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ResourceResponse> getResourcesByStageId(Long stageId) {
        return contestReaderService.getStageResources(stageId).stream()
                .map(ResourceMapper::toResourceResponse)
                .toList();
    }

    private ContestStage validateAccessAndGetStage(AuthenticatedUser user, Long stageId) {
        ContestStage stage = contestReaderService.getStageById(stageId);
        rolePolicy.requireAny(stage.getContestId(), user.id(), ContestRole.CREATOR, ContestRole.ADMIN);
        return stage;
    }

    private ContestResource requireStageResource(Long stageId, Long resourceId) {
        ContestResource resource = resourceRepository.findById(resourceId)
                .orElseThrow(() -> new LaunchGateException("Ресурс не найден"));

        if (!stageId.equals(resource.getStageId())) {
            throw new LaunchGateException("Ресурс не найден");
        }
        return resource;
    }

}
