package com.launchgate.contest.service.api;

import com.launchgate.contest.dto.registration.ContestParticipantOrganizerResponse;
import com.launchgate.contest.dto.registration.ContestParticipantResponse;
import com.launchgate.identity.dto.AuthenticatedUser;

import java.util.List;

/**
 * Сервис для управления регистрацией участников в конкурсах.
 * Обеспечивает процесс подачи заявок, а также предоставляет списки зарегистрированных.
 */
public interface ContestRegistrationService {

    /**
     * Регистрирует текущего пользователя на участие в конкурсе.
     *
     * @param user      текущий аутентифицированный пользователь, подающий заявку
     * @param contestId идентификатор конкурса для регистрации
     * @return идентификатор созданной записи регистрации или команды
     */
    Long register(AuthenticatedUser user, Long contestId);

    /**
     * Возвращает список участников конкурса, доступный для просмотра другим участникам.
     *
     * @param user      текущий аутентифицированный пользователь, запрашивающий список
     * @param contestId идентификатор конкурса
     * @return список ответов с публичными данными участников, отсортированный по дате регистрации
     */
    List<ContestParticipantResponse> participantVisibleParticipants(AuthenticatedUser user, Long contestId);

    /**
     * Возвращает расширенный список участников конкурса для организаторов и администраторов.
     *
     * @param user      текущий аутентифицированный пользователь (организатор)
     * @param contestId идентификатор конкурса
     * @return список ответов с развернутыми данными участников для организационной панели
     */
    List<ContestParticipantOrganizerResponse> organizerVisibleParticipants(AuthenticatedUser user, Long contestId);
}

