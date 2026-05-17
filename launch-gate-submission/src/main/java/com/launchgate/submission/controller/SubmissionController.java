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

/**
 * Контроллер для работы с формами работ.
 */
@RestController
@RequestMapping("/api/v1/projects")
@RequiredArgsConstructor
@Tag(name = "Работы", description = "Работы по этапам")
public class SubmissionController {
    private final SubmissionService submissionService;

    @GetMapping("/organizer/stage-submissions/{submissionId}")
    @Operation(summary = "Получение подробной информации по форме этапа для пространства организатора")
    public StageSubmissionResponse organizerSubmission(
            @AuthenticationPrincipal AuthenticatedUser user,
            @PathVariable Long submissionId
    ) {
        return submissionService.organizerSubmission(user, submissionId);
    }

    @PostMapping("/{projectId}/stages/{stageId}/values")
    @Operation(summary = "Сохранение значения в черновике формы этапа")
    public StageSubmissionResponse saveValue(
            @AuthenticationPrincipal AuthenticatedUser user,
            @PathVariable Long projectId,
            @PathVariable Long stageId,
            @Valid @RequestBody ValueRequest request
    ) {
        return submissionService.saveValue(user, projectId, stageId, request);
    }

    @PostMapping("/{projectId}/stages/{stageId}/submit")
    @Operation(summary = "Завершить отправку формы этапа")
    public StageSubmissionResponse submit(
            @AuthenticationPrincipal AuthenticatedUser user,
            @PathVariable Long projectId,
            @PathVariable Long stageId
    ) {
        return submissionService.submit(user, projectId, stageId);
    }
}
