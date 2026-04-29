package com.launchgate.submission.controller;

import lombok.RequiredArgsConstructor;

import com.launchgate.submission.dto.*;
import com.launchgate.submission.service.*;
import com.launchgate.identity.dto.AuthenticatedUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/projects")
@RequiredArgsConstructor
@Tag(name = "Submissions", description = "Participant projects and stage form submissions")
public class SubmissionController {
    private final SubmissionService submissionService;

    @PostMapping
    @Operation(summary = "Create participant or team project workspace")
    public ProjectResponse createProject(
            @AuthenticationPrincipal AuthenticatedUser user,
            @Valid @RequestBody ProjectRequest request
    ) {
        return submissionService.createProject(user, request);
    }

    @GetMapping("/my")
    @Operation(summary = "List current user projects grouped into active and archived")
    public MyProjectsResponse myProjects(@AuthenticationPrincipal AuthenticatedUser user) {
        return submissionService.myProjects(user);
    }

    @GetMapping("/{projectId}")
    @Operation(summary = "Get project with stage forms and saved values")
    public ProjectResponse project(@AuthenticationPrincipal AuthenticatedUser user, @PathVariable Long projectId) {
        return submissionService.project(user, projectId);
    }

    @GetMapping("/organizer/stage-submissions/{submissionId}")
    @Operation(summary = "Get stage submission details for organizer workspace")
    public StageSubmissionResponse organizerSubmission(
            @AuthenticationPrincipal AuthenticatedUser user,
            @PathVariable Long submissionId
    ) {
        return submissionService.organizerSubmission(user, submissionId);
    }

    @PostMapping("/{projectId}/stages/{stageId}/values")
    @Operation(summary = "Save one value inside a stage form draft")
    public StageSubmissionResponse saveValue(
            @AuthenticationPrincipal AuthenticatedUser user,
            @PathVariable Long projectId,
            @PathVariable Long stageId,
            @Valid @RequestBody ValueRequest request
    ) {
        return submissionService.saveValue(user, projectId, stageId, request);
    }

    @PostMapping("/{projectId}/stages/{stageId}/submit")
    @Operation(summary = "Finalize stage submission")
    public StageSubmissionResponse submit(
            @AuthenticationPrincipal AuthenticatedUser user,
            @PathVariable Long projectId,
            @PathVariable Long stageId
    ) {
        return submissionService.submit(user, projectId, stageId);
    }
}
