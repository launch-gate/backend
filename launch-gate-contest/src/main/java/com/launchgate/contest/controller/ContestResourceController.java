package com.launchgate.contest.controller;

import com.launchgate.contest.dto.DeletedResponse;
import com.launchgate.contest.dto.resources.ResourceListResponse;
import com.launchgate.contest.dto.resources.ResourceRequest;
import com.launchgate.contest.dto.resources.ResourceResponse;
import com.launchgate.contest.service.ContestResourceService;
import com.launchgate.identity.dto.AuthenticatedUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "Contest Resources", description = "Stage resource management")
public class ContestResourceController {
    private final ContestResourceService contestResourceService;

    @PostMapping("/organizer/stages/{stageId}/resources")
    @Operation(summary = "Create stage resource")
    public ResourceResponse createStageResource(
            @AuthenticationPrincipal AuthenticatedUser user,
            @PathVariable Long stageId,
            @Valid @RequestBody ResourceRequest request
    ) {
        return contestResourceService.createStageResource(user, stageId, request);
    }

    @PatchMapping("/organizer/stages/{stageId}/resources/{resourceId}")
    @Operation(summary = "Update stage resource")
    public ResourceResponse updateStageResource(
            @AuthenticationPrincipal AuthenticatedUser user,
            @PathVariable Long stageId,
            @PathVariable Long resourceId,
            @Valid @RequestBody ResourceRequest request
    ) {
        return contestResourceService.updateStageResource(user, stageId, resourceId, request);
    }

    @DeleteMapping("/organizer/stages/{stageId}/resources/{resourceId}")
    @Operation(summary = "Delete stage resource")
    public DeletedResponse deleteStageResource(
            @AuthenticationPrincipal AuthenticatedUser user,
            @PathVariable Long stageId,
            @PathVariable Long resourceId
    ) {
        return new DeletedResponse(contestResourceService.deleteStageResource(user, stageId, resourceId));
    }

    @GetMapping("/stages/{stageId}/resources")
    @Operation(summary = "Get stage resource")
    public ResourceListResponse getStageResources(
            @PathVariable Long stageId
    ) {
        return new ResourceListResponse(contestResourceService.getResourcesByStageId(stageId));
    }
}
