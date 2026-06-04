package com.launchgate.contest.service.impl;

import com.launchgate.common.LaunchGateException;
import com.launchgate.contest.dto.team.TeamRequest;
import com.launchgate.contest.dto.team.TeamJoinRequestResponse;
import com.launchgate.contest.dto.team.TeamResponse;
import com.launchgate.contest.entity.Contest;
import com.launchgate.contest.entity.ContestRegistration;
import com.launchgate.contest.enums.ContestStatus;
import com.launchgate.contest.enums.ParticipationMode;
import com.launchgate.contest.entity.team.Team;
import com.launchgate.contest.entity.team.TeamJoinRequest;
import com.launchgate.contest.enums.TeamJoinRequestStatus;
import com.launchgate.contest.entity.team.TeamMember;
import com.launchgate.contest.repository.ContestRegistrationRepository;
import com.launchgate.contest.repository.ContestRepository;
import com.launchgate.contest.repository.TeamJoinRequestRepository;
import com.launchgate.contest.repository.TeamMemberRepository;
import com.launchgate.contest.repository.TeamRepository;
import com.launchgate.contest.service.api.TeamService;
import com.launchgate.contest.utils.team.TeamJoinRequestMapper;
import com.launchgate.contest.utils.team.TeamMapper;
import com.launchgate.identity.entity.UserAccount;
import com.launchgate.identity.repository.UserAccountRepository;
import lombok.RequiredArgsConstructor;

import com.launchgate.common.ConflictException;
import com.launchgate.common.ForbiddenException;
import com.launchgate.common.NotFoundException;
import com.launchgate.identity.dto.AuthenticatedUser;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Реализация {@link TeamService}.
 */

@Service
@RequiredArgsConstructor
public class TeamServiceImpl implements TeamService {
    private final ContestRepository contestRepository;
    private final ContestRegistrationRepository registrationRepository;
    private final TeamRepository teamRepository;
    private final TeamMemberRepository memberRepository;
    private final TeamJoinRequestRepository joinRequestRepository;
    private final UserAccountRepository userAccountRepository;
    private final Clock clock;

    @Override
    @Transactional
    public Long registerParticipant(AuthenticatedUser user, Long contestId) {
        Contest contest = contestRepository.findById(contestId)
                .orElseThrow(() -> new NotFoundException("Конкурс не найден"));

        requireRegistrationOpen(contest);
        if (registrationRepository.existsByContestIdAndParticipantId(contestId, user.id())) {
            throw new ConflictException("Участник уже зарегистрирован");
        }

        UserAccount userAccount = userAccountRepository.findById(user.id())
                .orElseThrow(() -> new NotFoundException("Аккаунт пользователя не найден"));

        return registrationRepository.save(new ContestRegistration(contest, userAccount, Instant.now(clock))).getId();
    }

    @Override
    @Transactional
    public TeamResponse createTeam(AuthenticatedUser user, Long contestId, TeamRequest request) {
        Contest contest = contestRepository.findById(contestId)
                .orElseThrow(() -> new NotFoundException("Конкурс не найден"));

        requireRegistrationOpen(contest);
        requireRegistered(contestId, user.id());
        if (contest.getParticipationMode() != ParticipationMode.TEAM) {
            throw new LaunchGateException("Конкурс не является командным");
        }

        if (memberRepository.existsByParticipantIdAndContestId(user.id(), contestId)) {
            throw new ConflictException("Участник уже состоит в команде в этом конкурсе");
        }
        UserAccount userAccount = userAccountRepository.findById(user.id())
                .orElseThrow(() -> new NotFoundException("Аккаунт пользователя не найден"));
        String inviteToken = generateTeamInviteToken(contestId, request.name());

        if (teamRepository.existsByInviteToken(inviteToken)) {
            throw new LaunchGateException("Команда с названием: %s уже существует".formatted(request.name()));
        }

        Team team = teamRepository.save(
                new Team(
                        contest,
                        userAccount,
                        request.name(),
                        inviteToken,
                        Instant.now(clock)
                )
        );

        memberRepository.save(new TeamMember(team, userAccount, Instant.now(clock)));
        return TeamMapper.toResponse(team, getMemberIds(team.getId()));
    }

    @Override
    @Transactional
    public TeamResponse joinByInvite(AuthenticatedUser user, String inviteToken) {
        Team team = teamRepository.findByInviteToken(inviteToken)
                .orElseThrow(() -> new NotFoundException("Приглашение в команду не найдено"));

        UserAccount userAccount = userAccountRepository.findById(user.id())
                .orElseThrow(() -> new NotFoundException("Аккаунт пользователя не найден"));

        joinTeam(team, userAccount);
        return TeamMapper.toResponse(team, getMemberIds(team.getId()));
    }

    @Override
    @Transactional
    public Long requestJoin(AuthenticatedUser user, Long teamId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new NotFoundException("Команда не найдена"));

        Contest contest = team.getContest();
        requireRegistrationOpen(contestRepository.findById(contest.getId())
                .orElseThrow(() -> new NotFoundException("Конкурс не найден")));

        requireRegistered(contest.getId(), user.id());

        if (memberRepository.existsByTeamIdAndParticipantId(teamId, user.id())) {
            throw new ConflictException("Участник уже является членом команды");
        }

        if (memberRepository.existsByParticipantIdAndContestId(user.id(), contest.getId())) {
            throw new ConflictException("Участник уже состоит в другой команде в этом конкурсе");
        }

        if (joinRequestRepository.existsByTeamIdAndParticipantIdAndStatus(teamId, user.id(), TeamJoinRequestStatus.PENDING)) {
            throw new ConflictException("Уже существует активный запрос на вступление");
        }

        UserAccount userAccount = userAccountRepository.findById(user.id())
                .orElseThrow(() -> new NotFoundException("Аккаунт пользователя не найден"));

        return joinRequestRepository.save(new TeamJoinRequest(team, userAccount, Instant.now(clock))).getId();
    }

    @Override
    @Transactional
    public TeamResponse approveJoinRequest(AuthenticatedUser user, Long requestId) {
        TeamJoinRequest joinRequest = joinRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Запрос на вступление не найден"));

        Team team = joinRequest.getTeam();
        UserAccount teamLeader = team.getLeader();

        if (!teamLeader.getId().equals(user.id())) {
            throw new ForbiddenException("Только капитан команды может одобрять запросы на вступление");
        }

        if (joinRequest.getStatus() != TeamJoinRequestStatus.PENDING) {
            throw new LaunchGateException("Запрос на вступление уже обработан");
        }

        joinRequest.approve();
        joinTeam(team, joinRequest.getParticipant());
        return TeamMapper.toResponse(team, getMemberIds(team.getId()));
    }

    @Override
    @Transactional
    public TeamJoinRequestResponse rejectJoinRequest(AuthenticatedUser user, Long requestId) {
        TeamJoinRequest joinRequest = joinRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Запрос на вступление не найден"));
        Team team = joinRequest.getTeam();

        if (!team.getLeaderId().equals(user.id())) {
            throw new ForbiddenException("Только капитан команды может отклонять запросы на вступление");
        }
        if (joinRequest.getStatus() != TeamJoinRequestStatus.PENDING) {
            throw new LaunchGateException("Запрос на вступление уже обработан");
        }

        joinRequest.reject();
        return TeamJoinRequestMapper.toResponse(joinRequest);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TeamResponse> teams(Long contestId) {
        return teamRepository.findAllByContestIdOrderByCreatedAtDesc(contestId).stream()
                .map(team -> TeamMapper.toResponse(team, getMemberIds(team.getId())))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TeamJoinRequestResponse> pendingJoinRequests(AuthenticatedUser user, Long teamId) {
        Team team = getTeam(teamId);
        if (!team.getLeaderId().equals(user.id())) {
            throw new ForbiddenException("Только капитан команды может просматривать запросы на вступление");
        }
        return joinRequestRepository.findAllByTeamIdAndStatusOrderByCreatedAtDesc(teamId, TeamJoinRequestStatus.PENDING).stream()
                .map(TeamJoinRequestMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Team getTeam(Long teamId) {
        return teamRepository.findById(teamId)
                .orElseThrow(() -> new NotFoundException("Команда не найдена"));
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isLeader(Long teamId, Long userId) {
        return getTeam(teamId).getLeaderId().equals(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isMember(Long teamId, Long userId) {
        return memberRepository.existsByTeamIdAndParticipantId(teamId, userId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Long> getMemberIds(Long teamId) {
        return memberRepository.findAllByTeamId(teamId).stream()
                .map(member -> member.getParticipant().getId())
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Long> getTeamIdsByParticipant(Long participantId) {
        return memberRepository.findAllByParticipantId(participantId).stream()
                .map(member -> member.getTeam().getId())
                .toList();
    }

    private String generateTeamInviteToken(Long contestId, String teamName) {
        return String.format("LG-TEAM-%s-%s", contestId, teamName);
    }

    private void joinTeam(Team team, UserAccount user) {
        Contest contest = team.getContest();
        requireRegistrationOpen(contestRepository.findById(contest.getId())
                .orElseThrow(() -> new NotFoundException("Конкурс не найден")));

        requireRegistered(contest.getId(), user.getId());
        if (memberRepository.existsByTeamIdAndParticipantId(team.getId(), user.getId())) {
            throw new ConflictException("Участник уже является членом команды");
        }
        if (memberRepository.existsByParticipantIdAndContestId(user.getId(), contest.getId())) {
            throw new ConflictException("Участник уже состоит в другой команде в этом конкурсе");
        }
        memberRepository.save(new TeamMember(team, user, Instant.now(clock)));
    }

    private void requireRegistered(Long contestId, Long participantId) {
        if (!registrationRepository.existsByContestIdAndParticipantId(contestId, participantId)) {
            throw new LaunchGateException("Участник должен сначала зарегистрироваться в конкурсе");
        }
    }

    private void requireRegistrationOpen(Contest contest) {
        if (contest.getStatus() == ContestStatus.DRAFT) {
            throw new LaunchGateException("Конкурс еще не опубликован");
        }

        LocalDate today = LocalDate.now(clock);
        if (contest.getRegistrationEndsAt() != null && today.isAfter(contest.getRegistrationEndsAt())) {
            throw new LaunchGateException("Период регистрации завершен");
        }
    }
}
