package com.launchgate.contest.service;

import com.launchgate.contest.enums.ContestRole;
import lombok.RequiredArgsConstructor;

import com.launchgate.contest.repository.*;

import com.launchgate.common.ForbiddenException;
import java.util.Arrays;
import org.springframework.stereotype.Component;

/**
 * Сервис проверки доступов пользователя к конкурсу.
 */
@Component
@RequiredArgsConstructor
public class ContestRolePolicy {
    private final ContestOrganizerRepository organizerRepository;

    /**
     * Проверить наличие у пользователя хотя бы одной роли.
     * @param contestId идентификатор конкурса
     * @param userId идентификатор пользователя
     * @param roles перечесление ролей
     */
    public void requireAny(Long contestId, Long userId, ContestRole... roles) {
        if (!organizerRepository.existsByContestIdAndUserIdAndRoleIn(contestId, userId, Arrays.asList(roles))) {
            throw new ForbiddenException("Пользователь не обладает требуемой ролью в рамках конкурса");
        }
    }

    /**
     * Проверить наличие у пользователя хотя бы одной роли.
     * @param contestId идентификатор конкурса
     * @param userId идентификатор пользователя
     * @param roles перечесление ролей
     */
    public boolean hasAny(Long contestId, Long userId, ContestRole... roles) {
        return organizerRepository.existsByContestIdAndUserIdAndRoleIn(contestId, userId, Arrays.asList(roles));
    }
}
