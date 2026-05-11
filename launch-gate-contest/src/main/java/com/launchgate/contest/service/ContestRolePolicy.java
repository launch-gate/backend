package com.launchgate.contest.service;

import lombok.RequiredArgsConstructor;

import com.launchgate.contest.dto.*;
import com.launchgate.contest.entity.*;
import com.launchgate.contest.repository.*;

import com.launchgate.common.ForbiddenException;
import java.util.Arrays;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ContestRolePolicy {
    private final ContestOrganizerRepository organizerRepository;

    public void requireAny(Long contestId, Long userId, ContestRole... roles) {
        if (!organizerRepository.existsByContestIdAndUserIdAndRoleIn(contestId, userId, Arrays.asList(roles))) {
            throw new ForbiddenException("Пользователь не обладает требуемой ролью в рамках конкурса");
        }
    }

    public boolean hasAny(Long contestId, Long userId, ContestRole... roles) {
        return organizerRepository.existsByContestIdAndUserIdAndRoleIn(contestId, userId, Arrays.asList(roles));
    }
}
