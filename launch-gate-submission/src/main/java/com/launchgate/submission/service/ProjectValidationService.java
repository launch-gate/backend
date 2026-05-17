package com.launchgate.submission.service;

import com.launchgate.common.ForbiddenException;
import com.launchgate.common.NotFoundException;
import com.launchgate.contest.service.TeamService;
import com.launchgate.identity.dto.AuthenticatedUser;
import com.launchgate.submission.entity.Project;
import com.launchgate.submission.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * Сервис проверки прав доступа и валидации состояний проектов.
 */
@Service
@RequiredArgsConstructor
public class ProjectValidationService {

    private final TeamService teamService;
    private final ProjectRepository projectRepository;

    /**
     * Проверяет существование проекта и наличие у пользователя прав на его редактирование.
     * @param user пользователь
     * @param projectId идентификатор проекта.
     * @return проект
     */
    public Project requireEditableProject(AuthenticatedUser user, Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new NotFoundException("Проект не найден"));

        if (!canReadProject(user, project)) {
            throw new ForbiddenException("Нет доступа к проекту");
        }
        return project;
    }

    /**
     * Проверяет, имеет ли пользователь право на чтение/доступ к проекту.
     */
    public boolean canReadProject(AuthenticatedUser user, Project project) {
        if (Objects.nonNull(project.getOwnerParticipant())) {
            return project.getOwnerParticipant().getId().equals(user.id());
        }
        return Objects.nonNull(project.getTeam()) && teamService.isMember(project.getTeam().getId(), user.id());
    }
}
