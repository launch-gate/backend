package com.launchgate.contest.service.impl;

import com.launchgate.common.LaunchGateException;
import com.launchgate.contest.dto.organizer.AddOrganizerRequest;
import com.launchgate.contest.dto.OrganizerResponse;
import com.launchgate.contest.entity.Contest;
import com.launchgate.contest.entity.ContestOrganizer;
import com.launchgate.contest.enums.ContestRole;
import com.launchgate.contest.repository.ContestOrganizerRepository;
import com.launchgate.contest.service.api.ContestOrganizerService;
import com.launchgate.contest.service.ContestReaderService;
import com.launchgate.contest.service.ContestRolePolicy;
import com.launchgate.contest.utils.organizer.OrganizerMapper;
import com.launchgate.identity.dto.AuthenticatedUser;
import com.launchgate.identity.entity.UserAccount;
import com.launchgate.identity.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Реализация {@link ContestOrganizerService}.
 */
@Service
@RequiredArgsConstructor
public class ContestOrganizerServiceImpl implements ContestOrganizerService {
    private final ContestOrganizerRepository organizerRepository;
    private final ContestReaderService contestReaderService;
    private final ContestRolePolicy rolePolicy;
    private final UserService userService;

    @Override
    @Transactional(readOnly = true)
    public List<OrganizerResponse> organizers(AuthenticatedUser user, Long contestId) {
        rolePolicy.requireAny(contestId, user.id(), ContestRole.CREATOR, ContestRole.ADMIN);
        return organizerRepository.findAllByContestId(contestId).stream()
                .map(OrganizerMapper::toOrganizerResponse)
                .toList();
    }

    @Override
    @Transactional
    public OrganizerResponse addOrganizer(AuthenticatedUser user, Long contestId, AddOrganizerRequest request) {
        rolePolicy.requireAny(contestId, user.id(), ContestRole.CREATOR, ContestRole.ADMIN);
        UserAccount targetUser = userService.getUserById(request.userId());

        organizerRepository.findByContestIdAndUserIdAndRole(contestId, targetUser.getId(), request.role())
                .ifPresent((ignored) -> {
                    throw new LaunchGateException("Пользователь уже имеет эту роль в конкурсе");
                });

        Contest contest = contestReaderService.getContestById(contestId);
        ContestOrganizer organizer = organizerRepository.save(new ContestOrganizer(contest, targetUser, request.role()));
        return OrganizerMapper.toOrganizerResponse(organizer);
    }

    @Override
    @Transactional
    public Long deleteOrganizer(AuthenticatedUser user, Long contestId, Long organizerId) {
        rolePolicy.requireAny(contestId, user.id(), ContestRole.CREATOR, ContestRole.ADMIN);

        ContestOrganizer organizer = organizerRepository.findById(organizerId)
                .orElseThrow(() -> new LaunchGateException("Назначение организатора не найдено"));

        if (!organizer.getContestId().equals(contestId)) {
            throw new LaunchGateException("Назначение организатора не найдено");
        }
        if (organizer.getRole() == ContestRole.CREATOR) {
            throw new LaunchGateException("Нельзя удалить создателя конкурса");
        }
        organizerRepository.delete(organizer);
        return organizerId;
    }
}
