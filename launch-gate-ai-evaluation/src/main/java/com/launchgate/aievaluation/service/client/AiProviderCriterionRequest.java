package com.launchgate.aievaluation.service.client;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AiProviderCriterionRequest(
        @JsonProperty("id")
        String id,
        @JsonProperty("description")
        String description
) {
}
