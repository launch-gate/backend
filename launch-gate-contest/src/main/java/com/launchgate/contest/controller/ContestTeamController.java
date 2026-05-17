package com.launchgate.contest.controller;

import com.launchgate.contest.dto.registration.ParticipantContestRegistrationResponse;
import com.launchgate.contest.dto.team.AllTeamsResponse;
import com.launchgate.contest.dto.team.TeamJoinRequestListResponse;
import com.launchgate.contest.dto.team.TeamRequest;
import com.launchgate.contest.dto.team.TeamRequestJoinResponse;
import com.launchgate.contest.dto.team.TeamJoinRequestResponse;
import com.launchgate.contest.dto.team.TeamResponse;
import com.launchgate.contest.service.api.TeamService;
import com.launchgate.identity.dto.AuthenticatedUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * Контроллер для управления командой внутри конкурса.
 */
@RestController
@RequestMapping("/api/v1/contests/teams")
@RequiredArgsConstructor
@Tag(name = "Команды конкурсов", description = "Регистрация на конкурс и управление командами")
public class ContestTeamController {
    private final TeamService teamService;

    @PostMapping("/{contestId}/registrations")
    @Operation(summary = "Зарегистрировать текущего участника в конкурсе")
    public ParticipantContestRegistrationResponse register(@AuthenticationPrincipal AuthenticatedUser user, @PathVariable Long contestId) {
        return new ParticipantContestRegistrationResponse(teamService.registerParticipant(user, contestId));
    }

    @GetMapping("/{contestId}")
    @Operation(summary = "Получить список команд конкурса")
    public AllTeamsResponse teams(@PathVariable Long contestId) {
        return new AllTeamsResponse(teamService.teams(contestId));
    }

    @PostMapping("/{contestId}")
    @Operation(summary = "Создать команду в командном конкурсе")
    public TeamResponse createTeam(@AuthenticationPrincipal AuthenticatedUser user, @PathVariable Long contestId,
            @Valid @RequestBody TeamRequest request) {
        return teamService.createTeam(user, contestId, request);
    }

    @PostMapping("/join-by-invite/{inviteToken}")
    @Operation(summary = "Присоединиться к команде с использованием токена приглашения")
    public TeamResponse joinByInvite(@AuthenticationPrincipal AuthenticatedUser user, @PathVariable String inviteToken) {
        return teamService.joinByInvite(user, inviteToken);
    }

    @PostMapping("/{teamId}/join-requests")
    @Operation(summary = "Подать заявку на вступление в команду")
    public TeamRequestJoinResponse requestJoin(@AuthenticationPrincipal AuthenticatedUser user, @PathVariable Long teamId) {
        return new TeamRequestJoinResponse(teamService.requestJoin(user, teamId));
    }

    @GetMapping("/{teamId}/join-requests")
    @Operation(summary = "Получить список активных заявок на присоединение для капитана команды")
    public TeamJoinRequestListResponse joinRequests(@AuthenticationPrincipal AuthenticatedUser user, @PathVariable Long teamId) {
        return new TeamJoinRequestListResponse(teamService.pendingJoinRequests(user, teamId));
    }

    @PostMapping("/team-join-requests/{requestId}/approve")
    @Operation(summary = "Одобрить запрос на вступление в команду")
    public TeamResponse approveJoinRequest(@AuthenticationPrincipal AuthenticatedUser user, @PathVariable Long requestId) {
        return teamService.approveJoinRequest(user, requestId);
    }

    @PostMapping("/team-join-requests/{requestId}/reject")
    @Operation(summary = "Отклонить запрос на вступление в команду")
    public TeamJoinRequestResponse rejectJoinRequest(@AuthenticationPrincipal AuthenticatedUser user, @PathVariable Long requestId) {
        return teamService.rejectJoinRequest(user, requestId);
    }
}
