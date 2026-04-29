package com.launchgate.contest.service;

import com.launchgate.common.DomainException;
import com.launchgate.contest.dto.registration.ContestParticipantOrganizerResponse;
import com.launchgate.contest.dto.registration.ContestParticipantResponse;
import com.launchgate.contest.entity.ContestRole;
import com.launchgate.contest.repository.ContestRegistrationRepository;
import com.launchgate.contest.utils.registration.ContestRegistrationMapper;
import com.launchgate.identity.dto.AuthenticatedUser;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ContestRegistrationService {
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
        var contest = contestReaderService.getContestById(contestId);
        if (contest.getStatus().name().equals("DRAFT")) {
            throw new DomainException("contest_not_open", "Contest is not published yet");
        }
        if (!registrationRepository.existsByContestIdAndParticipantId(contestId, user.id())) {
            throw new DomainException("registration_required", "Register in contest before viewing participants");
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
