package com.launchgate.contest.controller;

import com.launchgate.contest.dto.DeletedResponse;
import com.launchgate.contest.dto.stage.StageOrganizesListResponse;
import com.launchgate.contest.dto.stage.StageOrganizesResponse;
import com.launchgate.contest.dto.stage.StageParticipantListResponse;
import com.launchgate.contest.dto.stage.StageParticipantResponse;
import com.launchgate.contest.dto.stage.StageRequest;
import com.launchgate.contest.service.ContestStageService;
import com.launchgate.identity.dto.AuthenticatedUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "Contest Stages", description = "Contest stage management")
public class ContestStageController {
    private final ContestStageService contestStageService;

    @PostMapping("/organizer/contests/{contestId}/stages")
    @Operation(summary = "Create contest stage")
    public StageOrganizesResponse createStage(
            @AuthenticationPrincipal AuthenticatedUser user,
            @PathVariable Long contestId,
            @Valid @RequestBody StageRequest request
    ) {
        return contestStageService.create(user, contestId, request);
    }

    @GetMapping("/organizer/contests/{contestId}/stages")
    @Operation(summary = "List configured stages of a contest")
    public StageOrganizesListResponse organizerStages(
            @PathVariable Long contestId
    ) {
        return new StageOrganizesListResponse(contestStageService.organizerStages(contestId));
    }

    @GetMapping("/organizer/stages/{stageId}")
    @Operation(summary = "Get configured stage")
    public StageOrganizesResponse organizerStage(
            @PathVariable Long stageId
    ) {
        return contestStageService.getOrganizerStage(stageId);
    }

    @PatchMapping("/organizer/stages/{stageId}")
    @Operation(summary = "Update stage settings")
    public StageOrganizesResponse updateStage(
            @AuthenticationPrincipal AuthenticatedUser user,
            @PathVariable Long stageId,
            @Valid @RequestBody StageRequest request
    ) {
        return contestStageService.update(user, stageId, request);
    }

    @DeleteMapping("/organizer/stages/{stageId}")
    @Operation(summary = "Delete contest stage")
    public DeletedResponse deleteStage(
            @AuthenticationPrincipal AuthenticatedUser user,
            @PathVariable Long stageId
    ) {
        return new DeletedResponse(contestStageService.delete(user, stageId));
    }

    @GetMapping("/contests/{contestId}/stages")
    @Operation(summary = "List public contest stages")
    public StageParticipantListResponse publicStages(@PathVariable Long contestId) {
        return new StageParticipantListResponse(contestStageService.publicStages(contestId));
    }

    @GetMapping("/contests/stages/{stageId}")
    @Operation(summary = "Get public stage details")
    public StageParticipantResponse publicStage(@PathVariable Long stageId) {
        return contestStageService.publicStage(stageId);
    }
}
