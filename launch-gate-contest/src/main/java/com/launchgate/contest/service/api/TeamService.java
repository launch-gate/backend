package com.launchgate.contest.service.api;

import com.launchgate.contest.dto.team.TeamJoinRequestResponse;
import com.launchgate.contest.dto.team.TeamRequest;
import com.launchgate.contest.dto.team.TeamResponse;
import com.launchgate.contest.entity.team.Team;
import com.launchgate.identity.dto.AuthenticatedUser;

import java.util.List;

/**
 * Сервис для управления командами, регистрацией участников и запросами на вступление в рамках конкурсов.
 */
public interface TeamService {

    /**
     * Регистрирует пользователя в качестве одиночного участника конкурса.
     *
     * @param user      данные аутентифицированного пользователя
     * @param contestId идентификатор конкурса
     * @return идентификатор созданной записи регистрации
     */
    Long registerParticipant(AuthenticatedUser user, Long contestId);

    /**
     * Создает новую команду для участия в конкурсе и автоматически добавляет создателя в качестве лидера и члена команды.
     *
     * @param user      данные аутентифицированного пользователя
     * @param contestId идентификатор конкурса
     * @param request   параметрами создания команды
     * @return информация о созданной команде
     */
    TeamResponse createTeam(AuthenticatedUser user, Long contestId, TeamRequest request);

    /**
     * Добавляет пользователя в команду по уникальному инвайт-токену (приглашению).
     *
     * @param user        данные аутентифицированного пользователя
     * @param inviteToken уникальный токен приглашения команды
     * @return информация о команде
     */
    TeamResponse joinByInvite(AuthenticatedUser user, String inviteToken);

    /**
     * Создает запрос от пользователя на вступление в команду.
     *
     * @param user   данные аутентифицированного пользователя
     * @param teamId идентификатор команды
     * @return идентификатор созданного запроса на вступление
     */
    Long requestJoin(AuthenticatedUser user, Long teamId);

    /**
     * Одобряет запрос пользователя на вступление в команду. Доступно только капитану команды.
     *
     * @param user      данные текущего аутентифицированного пользователя (капитана)
     * @param requestId идентификатор запроса на вступление
     * @return информация о команде
     */
    TeamResponse approveJoinRequest(AuthenticatedUser user, Long requestId);

    /**
     * Отклоняет запрос пользователя на вступление в команду. Доступно только капитану команды.
     *
     * @param user      данные текущего аутентифицированного пользователя (капитана)
     * @param requestId идентификатор запроса на вступление
     * @return информация об отклоненном запросе
     */
    TeamJoinRequestResponse rejectJoinRequest(AuthenticatedUser user, Long requestId);

    /**
     * Возвращает список всех команд, зарегистрированных в указанном конкурсе.
     * Сортировка производится по дате создания (от новых к старым).
     *
     * @param contestId идентификатор конкурса
     * @return список всех команд конкурса
     */
    List<TeamResponse> teams(Long contestId);

    /**
     * Возвращает список всех активных (ожидающих рассмотрения) запросов на вступление в команду.
     * Доступно только капитану команды.
     *
     * @param user   данные текущего аутентифицированного пользователя
     * @param teamId идентификатор команды
     * @return список активных запросов
     */
    List<TeamJoinRequestResponse> pendingJoinRequests(AuthenticatedUser user, Long teamId);

    /**
     * Получает сущность команды по её идентификатору.
     *
     * @param teamId идентификатор команды
     * @return сущность команды
     */
    Team getTeam(Long teamId);

    /**
     * Проверяет, является ли пользователь капитаном (лидером) указанной команды.
     *
     * @param teamId идентификатор команды
     * @param userId идентификатор пользователя
     * @return является ли пользователь капитаном (лидером) указанной команды.
     */
    boolean isLeader(Long teamId, Long userId);

    /**
     * Проверяет, состоит ли пользователь в указанной команде.
     *
     * @param teamId идентификатор команды
     * @param userId идентификатор пользователя
     * @return является ли пользователь участником команды
     */
    boolean isMember(Long teamId, Long userId);

    /**
     * Получает список идентификаторов всех участников определенной команды.
     *
     * @param teamId идентификатор команды
     * @return список ID участников команды
     */
    List<Long> getMemberIds(Long teamId);

    /**
     * Получает список идентификаторов команд, в которых состоит или состоял указанный пользователь.
     *
     * @param participantId идентификатор участника
     * @return список ID команд
     */
    List<Long> getTeamIdsByParticipant(Long participantId);
}