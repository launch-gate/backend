package com.launchgate.contest.controller;

import com.launchgate.contest.dto.DeletedResponse;
import com.launchgate.contest.dto.resources.ResourceListResponse;
import com.launchgate.contest.dto.resources.ResourceRequest;
import com.launchgate.contest.dto.resources.ResourceResponse;
import com.launchgate.contest.service.api.ContestResourceService;
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
@Tag(name = "Ресурсы конкурсов", description = "Управление ресурсами этапов")
public class ContestResourceController {
    private final ContestResourceService contestResourceService;

    @PostMapping("/organizer/stages/{stageId}/resources")
    @Operation(summary = "Создать ресурс этапа")
    public ResourceResponse createStageResource(@AuthenticationPrincipal AuthenticatedUser user, @PathVariable Long stageId,
            @Valid @RequestBody ResourceRequest request) {
        return contestResourceService.createStageResource(user, stageId, request);
    }

    @PatchMapping("/organizer/stages/{stageId}/resources/{resourceId}")
    @Operation(summary = "Обновить ресурс этапа")
    public ResourceResponse updateStageResource(@AuthenticationPrincipal AuthenticatedUser user, @PathVariable Long stageId,
            @PathVariable Long resourceId, @Valid @RequestBody ResourceRequest request) {
        return contestResourceService.updateStageResource(user, stageId, resourceId, request);
    }

    @DeleteMapping("/organizer/stages/{stageId}/resources/{resourceId}")
    @Operation(summary = "Удалить ресурс этапа")
    public DeletedResponse deleteStageResource(@AuthenticationPrincipal AuthenticatedUser user, @PathVariable Long stageId,
            @PathVariable Long resourceId) {
        return new DeletedResponse(contestResourceService.deleteStageResource(user, stageId, resourceId));
    }

    @GetMapping("/stages/{stageId}/resources")
    @Operation(summary = "Получить ресурс этапа")
    public ResourceListResponse getStageResources(@PathVariable Long stageId) {
        return new ResourceListResponse(contestResourceService.getResourcesByStageId(stageId));
    }
}
