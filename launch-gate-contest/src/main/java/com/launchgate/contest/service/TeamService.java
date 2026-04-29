package com.launchgate.contest.service;

import com.launchgate.contest.dto.team.TeamRequest;
import com.launchgate.contest.dto.team.TeamJoinRequestResponse;
import com.launchgate.contest.dto.team.TeamResponse;
import com.launchgate.contest.entity.team.Team;
import com.launchgate.contest.entity.team.TeamJoinRequest;
import com.launchgate.contest.entity.team.TeamJoinRequestStatus;
import com.launchgate.contest.entity.team.TeamMember;
import com.launchgate.contest.utils.team.TeamJoinRequestMapper;
import com.launchgate.contest.utils.team.TeamMapper;
import com.launchgate.identity.entity.UserAccount;
import com.launchgate.identity.repository.UserAccountRepository;
import lombok.RequiredArgsConstructor;

import com.launchgate.contest.entity.*;
import com.launchgate.contest.repository.*;

import com.launchgate.common.ConflictException;
import com.launchgate.common.DomainException;
import com.launchgate.common.ForbiddenException;
import com.launchgate.common.NotFoundException;
import com.launchgate.identity.dto.AuthenticatedUser;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TeamService {
    private final ContestRepository contestRepository;
    private final ContestRegistrationRepository registrationRepository;
    private final TeamRepository teamRepository;
    private final TeamMemberRepository memberRepository;
    private final TeamJoinRequestRepository joinRequestRepository;
    private final UserAccountRepository userAccountRepository;
    private final Clock clock;

    @Transactional
    public Long registerParticipant(AuthenticatedUser user, Long contestId) {
        var contest = contestRepository.findById(contestId).orElseThrow(() -> new NotFoundException("Contest not found"));
        requireRegistrationOpen(contest);
        if (registrationRepository.existsByContestIdAndParticipantId(contestId, user.id())) {
            throw new ConflictException("Participant is already registered");
        }
        var userAccount = userAccountRepository.findById(user.id()).orElseThrow(() -> new NotFoundException("User account not found"));
        return registrationRepository.save(new ContestRegistration(contest, userAccount, Instant.now(clock))).getId();
    }

    @Transactional
    public TeamResponse createTeam(AuthenticatedUser user, Long contestId, TeamRequest request) {
        var contest = contestRepository.findById(contestId).orElseThrow(() -> new NotFoundException("Contest not found"));
        requireRegistrationOpen(contest);
        requireRegistered(contestId, user.id());
        if (contest.getParticipationMode() != ParticipationMode.TEAM) {
            throw new DomainException("not_team_contest", "Contest does not support teams");
        }
        if (memberRepository.existsByParticipantIdAndContestId(user.id(), contestId)) {
            throw new ConflictException("Participant already belongs to a team in this contest");
        }
        var userAccount = userAccountRepository.findById(user.id()).orElseThrow(() -> new NotFoundException("User account not found"));
        var inviteToken = generateTeamInviteToken(contestId, request.name());
        if (teamRepository.existsByInviteToken(inviteToken)) {
            throw new DomainException("team_name_exists", String.format("Team name = %s is already taken", request.name()));
        }
        var team = teamRepository.save(
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

    @Transactional
    public TeamResponse joinByInvite(AuthenticatedUser user, String inviteToken) {
        var team = teamRepository.findByInviteToken(inviteToken).orElseThrow(() -> new NotFoundException("Invite not found"));
        var userAccount = userAccountRepository.findById(user.id()).orElseThrow(() -> new NotFoundException("User account not found"));
        joinTeam(team, userAccount);
        return TeamMapper.toResponse(team, getMemberIds(team.getId()));
    }

    @Transactional
    public Long requestJoin(AuthenticatedUser user, Long teamId) {
        var team = teamRepository.findById(teamId).orElseThrow(() -> new NotFoundException("Team not found"));
        var contest = team.getContest();
        requireRegistrationOpen(contestRepository.findById(contest.getId()).orElseThrow(() -> new NotFoundException("Contest not found")));
        requireRegistered(contest.getId(), user.id());
        if (memberRepository.existsByTeamIdAndParticipantId(teamId, user.id())) {
            throw new ConflictException("Participant is already a team member");
        }
        if (memberRepository.existsByParticipantIdAndContestId(user.id(), contest.getId())) {
            throw new ConflictException("Participant already belongs to another team in this contest");
        }
        if (joinRequestRepository.existsByTeamIdAndParticipantIdAndStatus(teamId, user.id(), TeamJoinRequestStatus.PENDING)) {
            throw new ConflictException("Pending join request already exists");
        }
        var userAccount = userAccountRepository.findById(user.id()).orElseThrow(() -> new NotFoundException("User account not found"));
        return joinRequestRepository.save(new TeamJoinRequest(team, userAccount, Instant.now(clock))).getId();
    }

    @Transactional
    public TeamResponse approveJoinRequest(AuthenticatedUser user, Long requestId) {
        var joinRequest = joinRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Join request not found"));
        var team = joinRequest.getTeam();
        var teamLeader = team.getLeader();
        if (!teamLeader.getId().equals(user.id())) {
            throw new ForbiddenException("Only team leader can approve join requests");
        }
        if (joinRequest.getStatus() != TeamJoinRequestStatus.PENDING) {
            throw new DomainException("request_not_pending", "Join request has already been processed");
        }
        joinRequest.approve();
        joinTeam(team, joinRequest.getParticipant());
        return TeamMapper.toResponse(team, getMemberIds(team.getId()));
    }

    @Transactional
    public TeamJoinRequestResponse rejectJoinRequest(AuthenticatedUser user, Long requestId) {
        var joinRequest = joinRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Join request not found"));
        var team = joinRequest.getTeam();
        if (!team.getLeaderId().equals(user.id())) {
            throw new ForbiddenException("Only team leader can reject join requests");
        }
        if (joinRequest.getStatus() != TeamJoinRequestStatus.PENDING) {
            throw new DomainException("request_not_pending", "Join request has already been processed");
        }
        joinRequest.reject();
        return TeamJoinRequestMapper.toResponse(joinRequest);
    }

    @Transactional(readOnly = true)
    public List<TeamResponse> teams(Long contestId) {
        return teamRepository.findAllByContestIdOrderByCreatedAtDesc(contestId).stream()
                .map(team -> TeamMapper.toResponse(team, getMemberIds(team.getId())))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<TeamJoinRequestResponse> pendingJoinRequests(AuthenticatedUser user, Long teamId) {
        var team = getTeam(teamId);
        if (!team.getLeaderId().equals(user.id())) {
            throw new ForbiddenException("Only team leader can view join requests");
        }
        return joinRequestRepository.findAllByTeamIdAndStatusOrderByCreatedAtDesc(teamId, TeamJoinRequestStatus.PENDING).stream()
                .map(TeamJoinRequestMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public Team getTeam(Long teamId) {
        return teamRepository.findById(teamId).orElseThrow(() -> new NotFoundException("Team not found"));
    }

    @Transactional(readOnly = true)
    public boolean isLeader(Long teamId, Long userId) {
        return getTeam(teamId).getLeaderId().equals(userId);
    }

    @Transactional(readOnly = true)
    public boolean isMember(Long teamId, Long userId) {
        return memberRepository.existsByTeamIdAndParticipantId(teamId, userId);
    }

    @Transactional(readOnly = true)
    public List<Long> getMemberIds(Long teamId) {
        return memberRepository.findAllByTeamId(teamId).stream()
                .map(member -> member.getParticipant().getId())
                .toList();
    }

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
        var contest = team.getContest();
        requireRegistrationOpen(contestRepository.findById(contest.getId()).orElseThrow(() -> new NotFoundException("Contest not found")));
        requireRegistered(contest.getId(), user.getId());
        if (memberRepository.existsByTeamIdAndParticipantId(team.getId(), user.getId())) {
            throw new ConflictException("Participant is already a team member");
        }
        if (memberRepository.existsByParticipantIdAndContestId(user.getId(), contest.getId())) {
            throw new ConflictException("Participant already belongs to another team in this contest");
        }
        memberRepository.save(new TeamMember(team, user, Instant.now(clock)));
    }

    private void requireRegistered(Long contestId, Long participantId) {
        if (!registrationRepository.existsByContestIdAndParticipantId(contestId, participantId)) {
            throw new DomainException("registration_required", "Participant must register in contest first");
        }
    }

    private void requireRegistrationOpen(Contest contest) {
        if (contest.getStatus() == ContestStatus.DRAFT) {
            throw new DomainException("contest_not_open", "Contest is not published yet");
        }
        var today = LocalDate.now(clock);
        if (contest.getStartsAt() != null && today.isBefore(contest.getStartsAt())) {
            throw new DomainException("registration_not_started", "Registration opens on contest start date");
        }
        if (contest.getRegistrationEndsAt() != null && today.isAfter(contest.getRegistrationEndsAt())) {
            throw new DomainException("registration_closed", "Registration period is over");
        }
    }
}
