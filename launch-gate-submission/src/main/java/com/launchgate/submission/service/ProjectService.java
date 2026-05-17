package com.launchgate.submission.service;

import com.launchgate.identity.dto.AuthenticatedUser;
import com.launchgate.submission.dto.MyProjectsResponse;
import com.launchgate.submission.dto.ProjectRequest;
import com.launchgate.submission.dto.ProjectResponse;

/**
 * Сервис для управления рабочими пространствами проектов участников и команд.
 */
public interface ProjectService {

    /**
     * Создать рабочее пространство проекта участника или команды.
     *
     * @param user    пользователь
     * @param request запрос на создания рабочего пространства проекта.
     * @return результат создания рабочего пространства проекта.
     */
    ProjectResponse createProject(AuthenticatedUser user, ProjectRequest request);

    /**
     * Получить рабочее пространство проекта участника или команды.
     *
     * @param user      пользователь
     * @param projectId идентификатор проекта.
     * @return рабочее пространства проекта.
     */
    ProjectResponse getProject(AuthenticatedUser user, Long projectId);

    /**
     * Получить список текущих проектов пользователя, сгруппированных по активным и архивным
     *
     * @param user пользователь
     * @return список проектов
     */
    MyProjectsResponse myProjects(AuthenticatedUser user);
}
