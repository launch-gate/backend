package com.launchgate.aievaluation.service.client;

import com.launchgate.aievaluation.config.AiEvaluationProperties;
import com.launchgate.common.DomainException;
import com.launchgate.contest.entity.FieldCriterion;
import java.util.List;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class AiReviewHttpClient {
    private static final ParameterizedTypeReference<List<AiProviderReviewResult>> RESPONSE_TYPE = new ParameterizedTypeReference<>() {
    };

    private final RestClient restClient;
    private final AiEvaluationProperties properties;

    public AiReviewHttpClient(RestClient.Builder restClientBuilder, AiEvaluationProperties properties) {
        this.restClient = restClientBuilder.build();
        this.properties = properties;
    }

    public List<AiProviderReviewResult> reviewRepository(String title, String repoUrl, List<FieldCriterion> criteria) {
        return review(
                properties.repositoryReviewUrl(),
                new AiProviderReviewRequest(title, repoUrl, null, criteria(criteria))
        );
    }

    public List<AiProviderReviewResult> reviewText(String title, String textContent, List<FieldCriterion> criteria) {
        return review(
                properties.textReviewUrl(),
                new AiProviderReviewRequest(title, null, textContent, criteria(criteria))
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
                        criterion.getId().toString(),
                        criterion.getDescription()
                ))
                .toList();
    }
}
