package com.launchgate.aievaluation.service.client;

import com.launchgate.aievaluation.config.AiEvaluationProperties;
import com.launchgate.aievaluation.exception.InvalidAiReviewRequestException;
import com.launchgate.common.DomainException;
import com.launchgate.contest.entity.FieldCriterion;
import java.util.ArrayList;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Slf4j
@Service
public class AiReviewHttpClient {
    private static final ParameterizedTypeReference<List<AiProviderReviewResult>> RESPONSE_TYPE = new ParameterizedTypeReference<>() {};
    private static final String DEFAULT_TITLE = "Untitled submission field";

    private final RestClient restClient;
    private final AiEvaluationProperties properties;

    public AiReviewHttpClient(RestClient.Builder restClientBuilder, AiEvaluationProperties properties) {
        this.restClient = restClientBuilder.build();
        this.properties = properties;
    }

    public List<AiProviderReviewResult> reviewRepository(String title, String repoUrl, List<FieldCriterion> criteria) {
        var request = validateRepositoryRequest(title, repoUrl, criteria);
        return review(
                properties.repositoryReviewUrl(),
                request
        );
    }

    public List<AiProviderReviewResult> reviewText(String title, String textContent, List<FieldCriterion> criteria) {
        var request = validateTextRequest(title, textContent, criteria);
        return review(
                properties.textReviewUrl(),
                request
        );
    }

    private List<AiProviderReviewResult> review(String url, AiProviderReviewRequest request) {
        try {
            var body = restClient.post()
                    .uri(url)
                    .body(request)
                    .retrieve()
                    .body(RESPONSE_TYPE);
            return body == null ? List.of() : body;
        } catch (Exception exception) {
            throw new DomainException("ai_review_call_failed", "Could not get response from AI review service");
        }
    }

    private List<AiProviderCriterionRequest> criteria(List<FieldCriterion> criteria) {
        return criteria.stream()
                .map(criterion -> new AiProviderCriterionRequest(
                        criterion.getId() == null ? null : criterion.getId().toString(),
                        criterion.getDescription()
                ))
                .toList();
    }

    private AiProviderReviewRequest validateRepositoryRequest(String title, String repoUrl, List<FieldCriterion> criteria) {
        var request = new AiProviderReviewRequest(
                sanitizeTitle(title),
                repoUrl,
                null,
                criteria(criteria)
        );
        validateRequest(request, "repository");
        return request;
    }

    private AiProviderReviewRequest validateTextRequest(String title, String textContent, List<FieldCriterion> criteria) {
        var request = new AiProviderReviewRequest(
                sanitizeTitle(title),
                null,
                textContent,
                criteria(criteria)
        );
        validateRequest(request, "text");
        return request;
    }

    private String sanitizeTitle(String title) {
        if (title == null || title.isBlank()) {
            return DEFAULT_TITLE;
        }
        return title.trim();
    }

    private void validateRequest(AiProviderReviewRequest request, String payloadKind) {
        log.info("Request: payloadKind = {}, title = {}, textContent = {}, repo = {}, criteria = {}", payloadKind, request.title(), request.textContent(), request.repoUrl(), request.criteria());
        var invalidFields = new ArrayList<String>();

        if (request.title() == null || request.title().isBlank()) {
            invalidFields.add("title");
        }
        if ("repository".equals(payloadKind) && (request.repoUrl() == null || request.repoUrl().isBlank())) {
            invalidFields.add("repo_url");
        }
        if ("text".equals(payloadKind) && (request.textContent() == null || request.textContent().isBlank())) {
            invalidFields.add("textContent");
        }
        if (request.criteria() == null || request.criteria().isEmpty()) {
            invalidFields.add("criteria");
        } else {
            for (int i = 0; i < request.criteria().size(); i++) {
                var criterion = request.criteria().get(i);
                if (criterion.id() == null || criterion.id().isBlank()) {
                    invalidFields.add("criteria[" + i + "].id");
                }
                if (criterion.description() == null || criterion.description().isBlank()) {
                    invalidFields.add("criteria[" + i + "].description");
                }
            }
        }

        if (!invalidFields.isEmpty()) {
            var message = "Skipping AI review request because invalid fields were detected: " + String.join(", ", invalidFields);
            log.warn("{}; payloadKind={}; title='{}'", message, payloadKind, request.title());
            throw new InvalidAiReviewRequestException(message);
        }
    }
}
