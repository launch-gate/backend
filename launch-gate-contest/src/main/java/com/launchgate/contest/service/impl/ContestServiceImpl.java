package com.launchgate.contest.service.impl;

import com.launchgate.common.LaunchGateException;
import com.launchgate.common.NotFoundException;
import com.launchgate.contest.dto.contest.ContestInfoResponse;
import com.launchgate.contest.dto.contest.ContestRequest;
import com.launchgate.contest.entity.Contest;
import com.launchgate.contest.entity.ContestOrganizer;
import com.launchgate.contest.enums.ContestRole;
import com.launchgate.contest.enums.ContestStatus;
import com.launchgate.contest.enums.ParticipationMode;
import com.launchgate.contest.repository.ContestOrganizerRepository;
import com.launchgate.contest.repository.ContestRepository;
import com.launchgate.contest.repository.ContestStageRepository;
import com.launchgate.contest.service.ContestReaderService;
import com.launchgate.contest.service.ContestRolePolicy;
import com.launchgate.contest.service.api.ContestService;
import com.launchgate.contest.utils.contest.ContestMapper;
import com.launchgate.identity.dto.AuthenticatedUser;
import com.launchgate.identity.entity.AccountType;
import com.launchgate.identity.entity.UserAccount;
import com.launchgate.identity.repository.UserAccountRepository;
import java.util.Comparator;
import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Реализация {@link ContestService}.
 */
@Service
@RequiredArgsConstructor
public class ContestServiceImpl implements ContestService {
    private final ContestRepository contestRepository;
    private final ContestOrganizerRepository organizerRepository;
    private final ContestStageRepository stageRepository;
    private final UserAccountRepository userAccountRepository;
    private final ContestRolePolicy rolePolicy;
    private final ContestReaderService contestReaderService;

    @Override
    @Transactional
    public ContestInfoResponse create(AuthenticatedUser user, ContestRequest request) {
        validateTeamSettings(request);
        Contest contest = new Contest(
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
        UserAccount creator = userAccountRepository.getReferenceById(user.id());
        organizerRepository.save(new ContestOrganizer(contest, creator, ContestRole.CREATOR));

        return ContestMapper.toInfoResponse(contest);
    }

    @Override
    @Transactional
    public ContestInfoResponse update(AuthenticatedUser user, Long contestId, ContestRequest request) {
        rolePolicy.requireAny(contestId, user.id(), ContestRole.CREATOR, ContestRole.ADMIN);
        validateTeamSettings(request);
        Contest contest = contestReaderService.getContestById(contestId);

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

    @Override
    @Transactional
    public ContestInfoResponse publish(AuthenticatedUser user, Long contestId) {
        rolePolicy.requireAny(contestId, user.id(), ContestRole.CREATOR, ContestRole.ADMIN);
        Contest contest = contestReaderService.getContestById(contestId);

        if (stageRepository.countByContestId(contestId) == 0) {
            throw new LaunchGateException("Конкурс должен содержать как минимум один этап перед публикацией");
        }

        contest.publish();
        return ContestMapper.toInfoResponse(contest);
    }

    @Override
    @Transactional
    public Long delete(AuthenticatedUser user, Long contestId) {
        rolePolicy.requireAny(contestId, user.id(), ContestRole.CREATOR, ContestRole.ADMIN);
        contestRepository.deleteById(contestId);
        return contestId;
    }

    @Override
    @Transactional(readOnly = true)
    public ContestInfoResponse getContestInfo(AuthenticatedUser user, Long contestId) {
        Contest contest = contestReaderService.getContestById(contestId);
        if (contest.getStatus() == ContestStatus.DRAFT && user.accountType() == AccountType.PARTICIPANT) {
            throw new NotFoundException("Конкурс для участника не найден");
        }
        return ContestMapper.toInfoResponse(contest);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ContestInfoResponse> getOrganizerContests(AuthenticatedUser user) {
        List<Long> contestIds = organizerRepository.findAllByUserId(user.id()).stream()
                .map(ContestOrganizer::getContestId)
                .distinct()
                .toList();

        return contestRepository.findAllById(contestIds).stream()
                .sorted(Comparator.comparing(Contest::getCreatedAt).reversed())
                .map(ContestMapper::toInfoResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ContestInfoResponse> getAll() {
        return contestRepository.findAll().stream()
                .map(ContestMapper::toInfoResponse)
                .toList();
    }

    private void validateTeamSettings(ContestRequest request) {
        if (request.participationMode() == ParticipationMode.TEAM) {

            boolean isCorrectSettings = (request.minTeamSize() == null) || (request.maxTeamSize() == null) || (request.minTeamSize() < 1)
                    || (request.maxTeamSize() < request.minTeamSize());

            if (isCorrectSettings) {
                throw new LaunchGateException("Командный конкурс требует указания корректного минимального и максимального размера команды");
            }
            return;
        }

        if ((request.minTeamSize() != null) || (request.maxTeamSize() != null)) {
            throw new LaunchGateException("Индивидуальный конкурс не должен определять ограничения на размер команды");
        }
    }
}
