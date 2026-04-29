package com.launchgate.submission.dto;

import java.util.List;

public record MyProjectsResponse(
        List<ProjectResponse> activeProjects,
        List<ProjectResponse> archivedProjects
) {
}
