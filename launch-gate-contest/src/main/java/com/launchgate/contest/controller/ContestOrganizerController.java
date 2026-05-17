package com.launchgate.contest.controller;

import com.launchgate.contest.dto.organizer.AddOrganizerRequest;
import com.launchgate.contest.dto.DeletedResponse;
import com.launchgate.contest.dto.OrganizerListResponse;
import com.launchgate.contest.dto.OrganizerResponse;
import com.launchgate.contest.service.api.ContestOrganizerService;
import com.launchgate.identity.dto.AuthenticatedUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Контроллер для работы с организаторами внутри конкурсов.
 */
@RestController
@RequestMapping("/api/v1/organizer/contests/{contestId}/organizers")
@RequiredArgsConstructor
@Tag(name = "Организаторы конкурсов", description = "Управление организаторами конкурсов")
public class ContestOrganizerController {
    private final ContestOrganizerService contestOrganizerService;

    @GetMapping
    @Operation(summary = "Получить список организаторов внутри конкурса")
    public OrganizerListResponse organizers(@AuthenticationPrincipal AuthenticatedUser user, @PathVariable Long contestId) {
        return new OrganizerListResponse(contestOrganizerService.organizers(user, contestId));
    }

    @PostMapping
    @Operation(summary = "Добавить роль организатора, эксперта или ментора пользователю внутри конкурс")
    public OrganizerResponse addOrganizer(@AuthenticationPrincipal AuthenticatedUser user, @PathVariable Long contestId,
            @Valid @RequestBody AddOrganizerRequest request) {
        return contestOrganizerService.addOrganizer(user, contestId, request);
    }

    @DeleteMapping("/{organizerId}")
    @Operation(summary = "Отозвать назначение организатора в конкурсе")
    public DeletedResponse deleteOrganizer(@AuthenticationPrincipal AuthenticatedUser user, @PathVariable Long contestId,
            @PathVariable Long organizerId) {
        return new DeletedResponse(contestOrganizerService.deleteOrganizer(user, contestId, organizerId));
    }
}
