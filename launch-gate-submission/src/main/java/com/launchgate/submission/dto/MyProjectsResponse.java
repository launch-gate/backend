package com.launchgate.submission.dto;

import java.util.List;

/**
 * Список текущих проектов пользователя, сгруппированных по активным и архивным.
 * @param activeProjects активные проекты
 * @param archivedProjects архивные проекты
 */
public record MyProjectsResponse(
        List<ProjectResponse> activeProjects,
        List<ProjectResponse> archivedProjects
) {
}
