package com.launchgate.evaluation.controller;

import com.launchgate.submission.dto.StageSubmissionResponse;
import lombok.RequiredArgsConstructor;

import com.launchgate.evaluation.dto.*;
import com.launchgate.evaluation.entity.*;
import com.launchgate.evaluation.service.*;
import com.launchgate.identity.dto.AuthenticatedUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "Проверка работ", description = "Назначение экспертов, подготовка черновиков и публикация проверки")
public class EvaluationController {
    private final EvaluationService evaluationService;

    @PostMapping("/organizer/evaluations/assignments")
    @Operation(summary = "Назначить эксперта на отправленное решение этапа")
    public AssignmentResponse assign(
            @AuthenticationPrincipal AuthenticatedUser user,
            @Valid @RequestBody AssignmentRequest request) {
        return evaluationService.assign(user, request);
    }

    @GetMapping("/expert/reviews")
    @Operation(summary = "Получить список назначенных проверок текущего эксперта")
    public AssignmentListResponse myReviews(
            @AuthenticationPrincipal AuthenticatedUser user,
            @RequestParam(required = false) ReviewStatus status) {
        return new AssignmentListResponse(evaluationService.myAssignments(user, status));
    }

    @GetMapping("/expert/reviews/{assignmentId}/submission")
    @Operation(summary = "Получить отправленное на этап решение, назначенное текущему эксперту")
    public StageSubmissionResponse reviewSubmission(
            @AuthenticationPrincipal AuthenticatedUser user,
            @PathVariable Long assignmentId) {
        return evaluationService.reviewSubmission(user, assignmentId);
    }

    @PutMapping("/expert/reviews/{assignmentId}/draft")
    @Operation(summary = "Сохранить черновик оценки и комментария по проверке")
    public ReviewResponse saveDraft(
            @AuthenticationPrincipal AuthenticatedUser user,
            @PathVariable Long assignmentId,
            @Valid @RequestBody ReviewDraftRequest request) {
        return evaluationService.saveDraft(user, assignmentId, request);
    }

    @PostMapping("/expert/reviews/{assignmentId}/publish")
    @Operation(summary = "Завершить проверку")
    public ReviewResponse publish(@AuthenticationPrincipal AuthenticatedUser user, @PathVariable Long assignmentId) {
        return evaluationService.publish(user, assignmentId);
    }
}
