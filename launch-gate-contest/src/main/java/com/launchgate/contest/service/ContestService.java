package com.launchgate.contest.service;

import com.launchgate.common.DomainException;
import com.launchgate.common.NotFoundException;
import com.launchgate.contest.dto.contest.ContestInfoResponse;
import com.launchgate.contest.dto.contest.ContestRequest;
import com.launchgate.contest.entity.Contest;
import com.launchgate.contest.entity.ContestOrganizer;
import com.launchgate.contest.entity.ContestRole;
import com.launchgate.contest.entity.ContestStatus;
import com.launchgate.contest.entity.ParticipationMode;
import com.launchgate.contest.repository.ContestOrganizerRepository;
import com.launchgate.contest.repository.ContestRepository;
import com.launchgate.contest.repository.ContestStageRepository;
import com.launchgate.contest.utils.contest.ContestMapper;
import com.launchgate.identity.dto.AuthenticatedUser;
import com.launchgate.identity.entity.AccountType;
import com.launchgate.identity.repository.UserAccountRepository;
import java.util.Comparator;
import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ContestService {
    private final ContestRepository contestRepository;
    private final ContestOrganizerRepository organizerRepository;
    private final ContestStageRepository stageRepository;
    private final UserAccountRepository userAccountRepository;
    private final ContestRolePolicy rolePolicy;
    private final ContestReaderService contestReaderService;

    @Transactional
    public ContestInfoResponse create(AuthenticatedUser user, ContestRequest request) {
        validateTeamSettings(request);
        var contest = new Contest(
                request.title(),
                request.description(),
                request.rules(),
                request.participationMode(),
                request.minTeamSize(),
                request.maxTeamSize(),
                request.registrationEndsAt(),
                request.teamBuildingEndsAt(),
                request.startsAt(),
                request.endsAt(),
                request.contacts()
        );
        contest = contestRepository.save(contest);
        var creator = userAccountRepository.getReferenceById(user.id());
        organizerRepository.save(new ContestOrganizer(contest, creator, ContestRole.CREATOR));

        return ContestMapper.toInfoResponse(contest);
    }

    @Transactional
    public ContestInfoResponse update(AuthenticatedUser user,
                                  Long contestId,
                                  ContestRequest request
    ) {
        rolePolicy.requireAny(contestId, user.id(), ContestRole.CREATOR, ContestRole.ADMIN);
        validateTeamSettings(request);
        var contest = contestReaderService.getContestById(contestId);
        contest.update(
                request.title(),
                request.description(),
                request.rules(),
                request.participationMode(),
                request.minTeamSize(),
                request.maxTeamSize(),
                request.registrationEndsAt(),
                request.teamBuildingEndsAt(),
                request.startsAt(),
                request.endsAt(),
                request.contacts()
        );
        return ContestMapper.toInfoResponse(contest);
    }

    @Transactional
    public ContestInfoResponse publish(AuthenticatedUser user, Long contestId) {
        rolePolicy.requireAny(contestId, user.id(), ContestRole.CREATOR, ContestRole.ADMIN);
        var contest = contestReaderService.getContestById(contestId);
        if (stageRepository.countByContestId(contestId) == 0) {
            throw new DomainException("contest_without_stages", "Contest needs at least one stage before publishing");
        }
        contest.publish();
        return ContestMapper.toInfoResponse(contest);
    }

    @Transactional
    public Long delete(AuthenticatedUser user, Long contestId) {
        rolePolicy.requireAny(contestId, user.id(), ContestRole.CREATOR, ContestRole.ADMIN);
        contestRepository.deleteById(contestId);
        return contestId;
    }

    @Transactional(readOnly = true)
    public ContestInfoResponse getContestInfo(AuthenticatedUser user, Long contestId) {
        var contest = contestReaderService.getContestById(contestId);
        if (contest.getStatus() == ContestStatus.DRAFT && user.accountType() == AccountType.PARTICIPANT) {
            throw new NotFoundException("Contest not found for participant");
        }
        return ContestMapper.toInfoResponse(contest);
    }

    @Transactional(readOnly = true)
    public List<ContestInfoResponse> getOrganizerContests(AuthenticatedUser user) {
        var contestIds = organizerRepository.findAllByUserId(user.id()).stream()
                .map(ContestOrganizer::getContestId)
                .distinct()
                .toList();
        return contestRepository.findAllById(contestIds).stream()
                .sorted(Comparator.comparing(Contest::getCreatedAt).reversed())
                .map(ContestMapper::toInfoResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ContestInfoResponse> getAll() {
        return contestRepository.findAll()
                .stream()
                .map(ContestMapper::toInfoResponse)
                .toList();
    }

    private void validateTeamSettings(ContestRequest request) {
        if (request.participationMode() == ParticipationMode.TEAM) {
            if (request.minTeamSize() == null || request.maxTeamSize() == null || request.minTeamSize() < 1
                    || request.maxTeamSize() < request.minTeamSize()) {
                throw new DomainException("bad_team_size", "Team contest requires valid min and max team sizes");
            }
            return;
        }
        if (request.minTeamSize() != null || request.maxTeamSize() != null) {
            throw new DomainException("bad_team_size", "Individual contest must not define team size limits");
        }
    }
}
