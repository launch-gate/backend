package com.launchgate.contest.service.api;

import com.launchgate.contest.dto.OrganizerResponse;
import com.launchgate.contest.dto.organizer.AddOrganizerRequest;
import com.launchgate.identity.dto.AuthenticatedUser;

import java.util.List;

/**
 * Сервис для управления организаторами конкурса.
 * Обеспечивает добавление, удаление и просмотр списка организаторов с проверкой прав доступа.
 */
public interface ContestOrganizerService {

    /**
     * Возвращает список всех организаторов конкретного конкурса.
     *
     * @param user      текущий аутентифицированный пользователь, запрашивающий список
     * @param contestId идентификатор конкурса
     * @return список ответов с данными организаторов
     */
    List<OrganizerResponse> organizers(AuthenticatedUser user, Long contestId);

    /**
     * Добавляет нового организатора с указанной ролью в конкурс.
     *
     * @param user      текущий аутентифицированный пользователь, выполняющий действие
     * @param contestId идентификатор конкурса, в который добавляется организатор
     * @param request   данные запроса, содержащие ID целевого пользователя и его роль
     * @return ответ с данными созданного организатора
     */
    OrganizerResponse addOrganizer(AuthenticatedUser user, Long contestId, AddOrganizerRequest request);

    /**
     * Удаляет организатора из конкурса.
     *
     * @param user        текущий аутентифицированный пользователь, выполняющий удаление
     * @param contestId   идентификатор конкурса
     * @param organizerId идентификатор записи организатора (связи конкурса и пользователя), которую нужно удалить
     * @return идентификатор удаленной записи организатора
     */
    Long deleteOrganizer(AuthenticatedUser user, Long contestId, Long organizerId);
}

