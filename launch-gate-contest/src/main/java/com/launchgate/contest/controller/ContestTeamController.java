package com.launchgate.contest.controller;

import com.launchgate.contest.dto.registration.ParticipantContestRegistrationResponse;
import com.launchgate.contest.dto.team.AllTeamsResponse;
import com.launchgate.contest.dto.team.TeamJoinRequestListResponse;
import com.launchgate.contest.dto.team.TeamRequest;
import com.launchgate.contest.dto.team.TeamRequestJoinResponse;
import com.launchgate.contest.dto.team.TeamJoinRequestResponse;
import com.launchgate.contest.dto.team.TeamResponse;
import com.launchgate.contest.service.TeamService;
import com.launchgate.identity.dto.AuthenticatedUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/contests/teams")
@RequiredArgsConstructor
@Tag(name = "Contests", description = "Contest register and team management")
public class ContestTeamController {
    private final TeamService teamService;

    @PostMapping("/{contestId}/registrations")
    @Operation(summary = "Register current participant in a contest")
    public ParticipantContestRegistrationResponse register(
            @AuthenticationPrincipal AuthenticatedUser user,
            @PathVariable Long contestId
    ) {
        return new ParticipantContestRegistrationResponse(teamService.registerParticipant(user, contestId));
    }

    @GetMapping("/{contestId}")
    @Operation(summary = "List contest teams")
    public AllTeamsResponse teams(@PathVariable Long contestId) {
        return new AllTeamsResponse(teamService.teams(contestId));
    }

    @PostMapping("/{contestId}")
    @Operation(summary = "Create a team in a team contest")
    public TeamResponse createTeam(
            @AuthenticationPrincipal AuthenticatedUser user,
            @PathVariable Long contestId,
            @Valid @RequestBody TeamRequest request
    ) {
        return teamService.createTeam(user, contestId, request);
    }

    @PostMapping("/join-by-invite/{inviteToken}")
    @Operation(summary = "Join team by invite token")
    public TeamResponse joinByInvite(@AuthenticationPrincipal AuthenticatedUser user, @PathVariable String inviteToken) {
        return teamService.joinByInvite(user, inviteToken);
    }

    @PostMapping("/{teamId}/join-requests")
    @Operation(summary = "Request to join a team")
    public TeamRequestJoinResponse requestJoin(@AuthenticationPrincipal AuthenticatedUser user, @PathVariable Long teamId) {
        return new TeamRequestJoinResponse(teamService.requestJoin(user, teamId));
    }

    @GetMapping("/{teamId}/join-requests")
    @Operation(summary = "List pending join requests for current team leader")
    public TeamJoinRequestListResponse joinRequests(
            @AuthenticationPrincipal AuthenticatedUser user,
            @PathVariable Long teamId
    ) {
        return new TeamJoinRequestListResponse(teamService.pendingJoinRequests(user, teamId));
    }

    @PostMapping("/team-join-requests/{requestId}/approve")
    @Operation(summary = "Approve team join request")
    public TeamResponse approveJoinRequest(@AuthenticationPrincipal AuthenticatedUser user, @PathVariable Long requestId) {
        return teamService.approveJoinRequest(user, requestId);
    }

    @PostMapping("/team-join-requests/{requestId}/reject")
    @Operation(summary = "Reject team join request")
    public TeamJoinRequestResponse rejectJoinRequest(@AuthenticationPrincipal AuthenticatedUser user, @PathVariable Long requestId) {
        return teamService.rejectJoinRequest(user, requestId);
    }
}
