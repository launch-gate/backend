package com.launchgate.contest.controller;

import com.launchgate.contest.dto.contest.ContestInfoResponse;
import com.launchgate.contest.dto.contest.ContestListInfoResponse;
import com.launchgate.contest.dto.contest.ContestRequest;
import com.launchgate.contest.dto.DeletedResponse;
import com.launchgate.contest.service.ContestService;
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
@Tag(name = "Contests", description = "Contest setup and catalog")
public class ContestController {
    private final ContestService contestService;

    @GetMapping("/organizer/contests")
    @Operation(summary = "List contests managed by current organizer")
    public ContestListInfoResponse organizerContests(@AuthenticationPrincipal AuthenticatedUser user) {
        return new ContestListInfoResponse(contestService.getOrganizerContests(user));
    }

    @GetMapping("/contests/{contestId}")
    @Operation(summary = "Get contest view")
    public ContestInfoResponse getContest(@AuthenticationPrincipal AuthenticatedUser user, @PathVariable Long contestId) {
        return contestService.getContestInfo(user, contestId);
    }

    @PostMapping("/organizer/contests")
    @Operation(summary = "Create a new contest")
    public ContestInfoResponse createContest(
            @AuthenticationPrincipal AuthenticatedUser user,
            @Valid @RequestBody ContestRequest request
    ) {
        return contestService.create(user, request);
    }

    @PatchMapping("/organizer/contests/{contestId}")
    @Operation(summary = "Update contest settings")
    public ContestInfoResponse updateContest(
            @AuthenticationPrincipal AuthenticatedUser user,
            @PathVariable Long contestId,
            @Valid @RequestBody ContestRequest request
    ) {
        return contestService.update(user, contestId, request);
    }

    @DeleteMapping("/organizer/contests/{contestId}")
    @Operation(summary = "Delete contest with all nested setup")
    public DeletedResponse deleteContest(@AuthenticationPrincipal AuthenticatedUser user, @PathVariable Long contestId) {
        return new DeletedResponse(contestService.delete(user, contestId));
    }

    @PostMapping("/organizer/contests/{contestId}/publish")
    @Operation(summary = "Publish contest and open it for participants")
    public ContestInfoResponse publish(@AuthenticationPrincipal AuthenticatedUser user, @PathVariable Long contestId) {
        return contestService.publish(user, contestId);
    }

    @GetMapping("/contests")
    @Operation(summary = "All contests")
    public ContestListInfoResponse getAll() {
        return new ContestListInfoResponse(contestService.getAll());
    }
}
