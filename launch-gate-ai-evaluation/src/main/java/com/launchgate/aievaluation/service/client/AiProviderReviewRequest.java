package com.launchgate.aievaluation.service.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record AiProviderReviewRequest(
        @JsonProperty("title")
        String title,
        @JsonProperty("repo_url")
        String repoUrl,
        @JsonProperty("text_content")
        String textContent,
        @JsonProperty("criteria")
        List<AiProviderCriterionRequest> criteria
) {
}
