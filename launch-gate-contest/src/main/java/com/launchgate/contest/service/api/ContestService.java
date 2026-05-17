package com.launchgate.contest.service.api;

import com.launchgate.contest.dto.contest.ContestInfoResponse;
import com.launchgate.contest.dto.contest.ContestRequest;
import com.launchgate.identity.dto.AuthenticatedUser;

import java.util.List;

/**
 * Сервис для управления жизненным циклом конкурсов.
 * Обеспечивает создание, редактирование, публикацию, удаление и получение информации
 * о конкурсах с учетом настроек командного/индивидуального участия и прав доступа.
 */
public interface ContestService {

    /**
     * Создает новый конкурс и автоматически назначает его создателя организатором с ролью CREATOR.
     *
     * @param user    текущий аутентифицированный пользователь, создающий конкурс
     * @param request данные запроса с параметрами конкурса (даты, правила, ограничения команд)
     * @return ответ с полной информацией о созданном конкурсе
     * */
    ContestInfoResponse create(AuthenticatedUser user, ContestRequest request);

    /**
     * Обновляет параметры существующего конкурса.
     *
     * @param user      текущий аутентифицированный пользователь, выполняющий обновление
     * @param contestId идентификатор обновляемого конкурса
     * @param request   новые данные конкурса
     * @return ответ с обновленной информацией о конкурсе
     */
    ContestInfoResponse update(AuthenticatedUser user, Long contestId, ContestRequest request);

    /**
     * Публикует конкурс, переводя его из черновика в активное состояние.
     *
     * @param user      текущий аутентифицированный пользователь, выполняющий публикацию
     * @param contestId идентификатор публикуемого конкурса
     * @return ответ с обновленным статусом конкурса
     */
    ContestInfoResponse publish(AuthenticatedUser user, Long contestId);

    /**
     * Удаляет конкурс из системы по его идентификатору.
     *
     * @param user      текущий аутентифицированный пользователь, выполняющий удаление
     * @param contestId идентификатор удаляемого конкурса
     * @return идентификатор удаленного конкурса
     */
    Long delete(AuthenticatedUser user, Long contestId);

    /**
     * Возвращает подробную информацию о конкретном конкурсе.
     * Скрывает черновики от обычных участников.
     *
     * @param user      текущий аутентифицированный пользователь, запрашивающий информацию
     * @param contestId идентификатор конкурса
     * @return ответ с информацией о конкурсе
     */
    ContestInfoResponse getContestInfo(AuthenticatedUser user, Long contestId);

    /**
     * Возвращает список всех конкурсов, в которых текущий пользователь является организатором.
     * Результаты сортируются по дате создания в обратном порядке (сначала новые).
     *
     * @param user текущий аутентифицированный пользователь (организатор)
     * @return список ответов с информацией о конкурсах
     */
    List<ContestInfoResponse> getOrganizerContests(AuthenticatedUser user);

    /**
     * Возвращает список абсолютно всех конкурсов, зарегистрированных в системе.
     * Предназначен для глобального просмотра (например, на главной странице или в панели администратора).
     *
     * @return список ответов с информацией обо всех конкурсах
     */
    List<ContestInfoResponse> getAll();
}

