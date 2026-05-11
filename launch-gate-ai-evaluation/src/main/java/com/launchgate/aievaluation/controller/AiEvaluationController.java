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

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "AI Evaluation", description = "AI-assisted review for stage submissions")
public class AiEvaluationController {
    private final AiEvaluationService aiEvaluationService;

    @GetMapping("/organizer/evaluations/{submissionId}/ai-review")
    @Operation(summary = "Load stored AI review results for submission if they exist")
    public AiReviewLookupResponse getReview(
            @AuthenticationPrincipal AuthenticatedUser user,
            @PathVariable Long submissionId
    ) {
        return aiEvaluationService.getReview(user, submissionId);
    }

    @PostMapping("/organizer/evaluations/{submissionId}/ai-review")
    @Operation(summary = "Run AI review by field criteria and persist results")
    public AiReviewResponse runReview(
            @AuthenticationPrincipal AuthenticatedUser user,
            @PathVariable Long submissionId
    ) {
        return aiEvaluationService.runReview(user, submissionId);
    }
}
