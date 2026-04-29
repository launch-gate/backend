package com.launchgate.contest.controller;

import com.launchgate.contest.dto.organizer.AddOrganizerRequest;
import com.launchgate.contest.dto.DeletedResponse;
import com.launchgate.contest.dto.OrganizerListResponse;
import com.launchgate.contest.dto.OrganizerResponse;
import com.launchgate.contest.service.ContestOrganizerService;
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

@RestController
@RequestMapping("/api/v1/organizer/contests/{contestId}/organizers")
@RequiredArgsConstructor
@Tag(name = "Contest Organizers", description = "Contest organizer role management")
public class ContestOrganizerController {
    private final ContestOrganizerService contestOrganizerService;

    @GetMapping
    @Operation(summary = "List organizer roles inside a contest")
    public OrganizerListResponse organizers(
            @AuthenticationPrincipal AuthenticatedUser user,
            @PathVariable Long contestId
    ) {
        return new OrganizerListResponse(contestOrganizerService.organizers(user, contestId));
    }

    @PostMapping
    @Operation(summary = "Add organizer, expert or mentor role to a contest user")
    public OrganizerResponse addOrganizer(
            @AuthenticationPrincipal AuthenticatedUser user,
            @PathVariable Long contestId,
            @Valid @RequestBody AddOrganizerRequest request
    ) {
        return contestOrganizerService.addOrganizer(user, contestId, request);
    }

    @DeleteMapping("/{organizerId}")
    @Operation(summary = "Remove organizer assignment from contest")
    public DeletedResponse deleteOrganizer(
            @AuthenticationPrincipal AuthenticatedUser user,
            @PathVariable Long contestId,
            @PathVariable Long organizerId
    ) {
        return new DeletedResponse(contestOrganizerService.deleteOrganizer(user, contestId, organizerId));
    }
}
