package com.launchgate.aievaluation.controller;

import com.launchgate.aievaluation.dto.AiReviewLookupResponse;
import com.launchgate.aievaluation.dto.AiReviewResponse;
import com.launchgate.aievaluation.service.AiEvaluationService;
import com.launchgate.identity.dto.AuthenticatedUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Котроллер для взаимодействия с ИИ модулем.
 */
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "ИИ-оценивание", description = "Проверка решений этапа с использованием ИИ")
public class AiEvaluationController {
    private final AiEvaluationService aiEvaluationService;

    @GetMapping("/organizer/evaluations/{submissionId}/ai-review")
    @Operation(summary = "Загрузить сохраненные результаты ИИ-проверки для решения, если они существуют")
    public AiReviewLookupResponse getReview(
            @AuthenticationPrincipal AuthenticatedUser user,
            @PathVariable Long submissionId
    ) {
        return aiEvaluationService.getReview(user, submissionId);
    }

    @PostMapping("/organizer/evaluations/{submissionId}/ai-review")
    @Operation(summary = "Запустить проверку ИИ по критериям полей и сохранить результаты")
    public AiReviewResponse runReview(
            @AuthenticationPrincipal AuthenticatedUser user,
            @PathVariable Long submissionId
    ) {
        return aiEvaluationService.runReview(user, submissionId);
    }
}
