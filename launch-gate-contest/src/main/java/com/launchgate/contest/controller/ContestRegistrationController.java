package com.launchgate.contest.controller;

import com.launchgate.contest.dto.registration.ContestParticipantListResponse;
import com.launchgate.contest.dto.registration.ContestParticipantOrganizerListResponse;
import com.launchgate.contest.dto.registration.ParticipantContestRegistrationResponse;
import com.launchgate.contest.service.ContestRegistrationService;
import com.launchgate.identity.dto.AuthenticatedUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "Contests", description = "Contest registrations and participant lists")
public class ContestRegistrationController {
    private final ContestRegistrationService contestRegistrationService;

    @PostMapping("/contests/{contestId}/registrations")
    @Operation(summary = "Register current participant in a contest")
    public ParticipantContestRegistrationResponse register(
            @AuthenticationPrincipal AuthenticatedUser user,
            @PathVariable Long contestId
    ) {
        return new ParticipantContestRegistrationResponse(contestRegistrationService.register(user, contestId));
    }

    @GetMapping("/contests/{contestId}/participants")
    @Operation(summary = "List participants visible for registered contest participant")
    public ContestParticipantListResponse participants(
            @AuthenticationPrincipal AuthenticatedUser user,
            @PathVariable Long contestId
    ) {
        return new ContestParticipantListResponse(contestRegistrationService.participantVisibleParticipants(user, contestId));
    }

    @GetMapping("/organizer/contests/{contestId}/participants")
    @Operation(summary = "List contest participants for organizer workspace")
    public ContestParticipantOrganizerListResponse organizerParticipants(
            @AuthenticationPrincipal AuthenticatedUser user,
            @PathVariable Long contestId
    ) {
        return new ContestParticipantOrganizerListResponse(contestRegistrationService.organizerVisibleParticipants(user, contestId));
    }
}
