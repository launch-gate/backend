package com.launchgate.submission.controller;

import com.launchgate.identity.dto.AuthenticatedUser;
import com.launchgate.submission.dto.MyProjectsResponse;
import com.launchgate.submission.dto.ProjectRequest;
import com.launchgate.submission.dto.ProjectResponse;
import com.launchgate.submission.service.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Контроллер для работы с проектами.
 */
@RestController
@RequestMapping("/api/v1/projects")
@RequiredArgsConstructor
@Tag(name = "Проекты", description = "Проекты участников")
public class ProjectController {
    private final ProjectService projectService;

    @PostMapping
    @Operation(summary = "Создание рабочего пространства проекта участника или команды")
    public ProjectResponse createProject(@AuthenticationPrincipal AuthenticatedUser user, @Valid @RequestBody ProjectRequest request) {
        return projectService.createProject(user, request);
    }

    @GetMapping("/my")
    @Operation(summary = "Список текущих проектов пользователя, сгруппированных по активным и архивным")
    public MyProjectsResponse myProjects(@AuthenticationPrincipal AuthenticatedUser user) {
        return projectService.myProjects(user);
    }

    @GetMapping("/{projectId}")
    @Operation(summary = "Получить проект с формами этапов и сохраненными значениями")
    public ProjectResponse project(@AuthenticationPrincipal AuthenticatedUser user, @PathVariable Long projectId) {
        return projectService.getProject(user, projectId);
    }
}
