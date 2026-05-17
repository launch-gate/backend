package com.launchgate.contest.controller;

import com.launchgate.contest.dto.DeletedResponse;
import com.launchgate.contest.dto.stage.StageOrganizesListResponse;
import com.launchgate.contest.dto.stage.StageOrganizesResponse;
import com.launchgate.contest.dto.stage.StageParticipantListResponse;
import com.launchgate.contest.dto.stage.StageParticipantResponse;
import com.launchgate.contest.dto.stage.StageRequest;
import com.launchgate.contest.service.api.ContestStageService;
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

/**
 * Контроллер для работы со стадиями конкурсов.
 */
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "Стадии конкурсов", description = "Управление стадиями конкурсов")
public class ContestStageController {
    private final ContestStageService contestStageService;

    @PostMapping("/organizer/contests/{contestId}/stages")
    @Operation(summary = "Создать стадию конкурса")
    public StageOrganizesResponse createStage(@AuthenticationPrincipal AuthenticatedUser user, @PathVariable Long contestId,
            @Valid @RequestBody StageRequest request) {
        return contestStageService.create(user, contestId, request);
    }

    @GetMapping("/organizer/contests/{contestId}/stages")
    @Operation(summary = "Получить список настроенных этапов конкурса")
    public StageOrganizesListResponse organizerStages(@PathVariable Long contestId) {
        return new StageOrganizesListResponse(contestStageService.organizerStages(contestId));
    }

    @GetMapping("/organizer/stages/{stageId}")
    @Operation(summary = "Получить настройку стадии")
    public StageOrganizesResponse organizerStage(@PathVariable Long stageId) {
        return contestStageService.getOrganizerStage(stageId);
    }

    @PatchMapping("/organizer/stages/{stageId}")
    @Operation(summary = "Обновить настройки стадии")
    public StageOrganizesResponse updateStage(@AuthenticationPrincipal AuthenticatedUser user, @PathVariable Long stageId,
            @Valid @RequestBody StageRequest request) {
        return contestStageService.update(user, stageId, request);
    }

    @DeleteMapping("/organizer/stages/{stageId}")
    @Operation(summary = "Удалить стадию конкурса")
    public DeletedResponse deleteStage(@AuthenticationPrincipal AuthenticatedUser user, @PathVariable Long stageId) {
        return new DeletedResponse(contestStageService.delete(user, stageId));
    }

    @GetMapping("/contests/{contestId}/stages")
    @Operation(summary = "Получить список публичных стадий конкурса")
    public StageParticipantListResponse publicStages(@PathVariable Long contestId) {
        return new StageParticipantListResponse(contestStageService.publicStages(contestId));
    }

    @GetMapping("/contests/stages/{stageId}")
    @Operation(summary = "Получить информацию о стадии конкурса")
    public StageParticipantResponse publicStage(@PathVariable Long stageId) {
        return contestStageService.publicStage(stageId);
    }
}
