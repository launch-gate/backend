package com.launchgate.export.service;

import com.launchgate.export.dto.ContestAnalyticsResponse;
import com.launchgate.identity.dto.AuthenticatedUser;

/**
 * Сервис для формирования аналитики по конкурсу
 */
public interface AnalyticsService {

    /**
     * Получить аналитику по конкурсу.
     *
     * @param user      пользователь
     * @param contestId идентификатор конкурса
     * @return аналитика
     */
    ContestAnalyticsResponse analytics(AuthenticatedUser user, Long contestId);
}
