package com.launchgate.contest.service;

import com.launchgate.common.DomainException;
import com.launchgate.contest.dto.resources.ResourceRequest;
import com.launchgate.contest.dto.resources.ResourceResponse;
import com.launchgate.contest.entity.ContestResource;
import com.launchgate.contest.entity.ContestRole;
import com.launchgate.contest.repository.ContestResourceRepository;
import com.launchgate.contest.utils.resource.ResourceMapper;
import com.launchgate.identity.dto.AuthenticatedUser;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ContestResourceService {
    private final ContestResourceRepository resourceRepository;
    private final ContestReaderService contestReaderService;
    private final SortOrderService sortOrderService;
    private final ContestRolePolicy rolePolicy;

    @Transactional
    public ResourceResponse createStageResource(AuthenticatedUser user, Long stageId, ResourceRequest request) {
        var stage = contestReaderService.getStageById(stageId);
        rolePolicy.requireAny(stage.getContestId(), user.id(), ContestRole.CREATOR, ContestRole.ADMIN);
        var resources = contestReaderService.getStageResources(stageId);
        var targetOrder = sortOrderService.normalizeRequestedOrder(request.order(), resources.size());
        sortOrderService.moveOrder(resources, targetOrder);

        var resource = new ContestResource(stage, targetOrder);
        resource.update(targetOrder, request.type(), request.title(), request.description(), request.linkUrl(), request.fileId());
        return ResourceMapper.toResourceResponse(resourceRepository.save(resource));
    }

    @Transactional
    public ResourceResponse updateStageResource(AuthenticatedUser user, Long stageId, Long resourceId, ResourceRequest request) {
        var stage = contestReaderService.getStageById(stageId);
        rolePolicy.requireAny(stage.getContestId(), user.id(), ContestRole.CREATOR, ContestRole.ADMIN);
        var resource = requireStageResource(stageId, resourceId);
        sortOrderService.replaceOrder(resource, contestReaderService.getStageResources(stageId), request.order());
        resource.update(resource.getOrder(), request.type(), request.title(), request.description(), request.linkUrl(), request.fileId());
        return ResourceMapper.toResourceResponse(resource);
    }

    @Transactional
    public Long deleteStageResource(AuthenticatedUser user, Long stageId, Long resourceId) {
        var stage = contestReaderService.getStageById(stageId);
        rolePolicy.requireAny(stage.getContestId(), user.id(), ContestRole.CREATOR, ContestRole.ADMIN);
        var resource = requireStageResource(stageId, resourceId);
        resourceRepository.delete(resource);
        sortOrderService.reorder(contestReaderService.getStageResources(stageId));
        return resourceId;
    }

    @Transactional(readOnly = true)
    public List<ResourceResponse> getResourcesByStageId(Long stageId) {
        return contestReaderService.getStageResources(stageId).stream()
                .map(ResourceMapper::toResourceResponse)
                .toList();
    }

    private ContestResource requireStageResource(Long stageId, Long resourceId) {
        var resource = resourceRepository.findById(resourceId)
                .orElseThrow(() -> new DomainException("resource_missing", "Resource not found"));
        if (!stageId.equals(resource.getStageId())) {
            throw new DomainException("resource_missing", "Resource not found");
        }
        return resource;
    }

}
