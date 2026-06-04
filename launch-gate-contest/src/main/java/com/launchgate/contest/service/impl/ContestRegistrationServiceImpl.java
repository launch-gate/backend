package com.launchgate.contest.service.impl;

import com.launchgate.common.LaunchGateException;
import com.launchgate.contest.dto.registration.ContestParticipantOrganizerResponse;
import com.launchgate.contest.dto.registration.ContestParticipantResponse;
import com.launchgate.contest.entity.Contest;
import com.launchgate.contest.enums.ContestRole;
import com.launchgate.contest.enums.ContestStatus;
import com.launchgate.contest.repository.ContestRegistrationRepository;
import com.launchgate.contest.service.ContestReaderService;
import com.launchgate.contest.service.ContestRolePolicy;
import com.launchgate.contest.service.api.ContestRegistrationService;
import com.launchgate.contest.service.api.TeamService;
import com.launchgate.contest.utils.registration.ContestRegistrationMapper;
import com.launchgate.identity.dto.AuthenticatedUser;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Реализация {@link ContestRegistrationService}.
 */
@Service
@RequiredArgsConstructor
public class ContestRegistrationServiceImpl implements ContestRegistrationService {
    private final TeamService teamService;
    private final ContestReaderService contestReaderService;
    private final ContestRegistrationRepository registrationRepository;
    private final ContestRolePolicy rolePolicy;

    @Transactional
    public Long register(AuthenticatedUser user, Long contestId) {
        return teamService.registerParticipant(user, contestId);
    }

    @Transactional(readOnly = true)
    public List<ContestParticipantResponse> participantVisibleParticipants(AuthenticatedUser user, Long contestId) {
        Contest contest = contestReaderService.getContestById(contestId);

        if (user == null) {
            throw new LaunchGateException("Для просмотра участников необходимо авторизоваться");
        }

        if (ContestStatus.DRAFT.equals(contest.getStatus())) {
            throw new LaunchGateException("Конкурс еще не опубликован");
        }

        if (!registrationRepository.existsByContestIdAndParticipantId(contestId, user.id())) {
            throw new LaunchGateException("Для просмотра участников необходимо зарегистрироваться в конкурсе");
        }

        return registrationRepository.findAllByContestIdOrderByRegisteredAtAsc(contestId).stream()
                .map(ContestRegistrationMapper::toParticipantResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ContestParticipantOrganizerResponse> organizerVisibleParticipants(AuthenticatedUser user, Long contestId) {
        rolePolicy.requireAny(contestId, user.id(), ContestRole.CREATOR, ContestRole.ADMIN);
        return registrationRepository.findAllByContestIdOrderByRegisteredAtAsc(contestId).stream()
                .map(ContestRegistrationMapper::toOrganizerResponse)
                .toList();
    }
}
