package com.launchgate.contest.service;

import com.launchgate.common.DomainException;
import com.launchgate.contest.dto.organizer.AddOrganizerRequest;
import com.launchgate.contest.dto.OrganizerResponse;
import com.launchgate.contest.entity.ContestOrganizer;
import com.launchgate.contest.entity.ContestRole;
import com.launchgate.contest.repository.ContestOrganizerRepository;
import com.launchgate.contest.utils.organizer.OrganizerMapper;
import com.launchgate.identity.dto.AuthenticatedUser;
import com.launchgate.identity.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ContestOrganizerService {
    private final ContestOrganizerRepository organizerRepository;
    private final ContestReaderService contestReaderService;
    private final ContestRolePolicy rolePolicy;
    private final UserService userService;

    @Transactional(readOnly = true)
    public List<OrganizerResponse> organizers(AuthenticatedUser user, Long contestId) {
        rolePolicy.requireAny(contestId, user.id(), ContestRole.CREATOR, ContestRole.ADMIN);
        return organizerRepository.findAllByContestId(contestId).stream()
                .map(OrganizerMapper::toOrganizerResponse)
                .toList();
    }

    @Transactional
    public OrganizerResponse addOrganizer(AuthenticatedUser user,
                                          Long contestId,
                                          AddOrganizerRequest request
    ) {
        rolePolicy.requireAny(contestId, user.id(), ContestRole.CREATOR, ContestRole.ADMIN);
        var targetUser = userService.getUserById(request.userId());
        organizerRepository.findByContestIdAndUserIdAndRole(contestId, targetUser.getId(), request.role())
                .ifPresent(ignored -> {
                    throw new DomainException("role_exists", "User already has this role in contest");
                });
        var contest = contestReaderService.getContestById(contestId);
        var organizer = organizerRepository.save(new ContestOrganizer(contest, targetUser, request.role()));
        return OrganizerMapper.toOrganizerResponse(organizer);
    }

    @Transactional
    public Long deleteOrganizer(AuthenticatedUser user, Long contestId, Long organizerId) {
        rolePolicy.requireAny(contestId, user.id(), ContestRole.CREATOR, ContestRole.ADMIN);
        var organizer = organizerRepository.findById(organizerId)
                .orElseThrow(() -> new DomainException("organizer_missing", "Organizer assignment not found"));
        if (!organizer.getContestId().equals(contestId)) {
            throw new DomainException("organizer_missing", "Organizer assignment not found");
        }
        if (organizer.getRole() == ContestRole.CREATOR) {
            throw new DomainException("last_creator", "Can't remove contest creator");
        }
        organizerRepository.delete(organizer);
        return organizerId;
    }
}
