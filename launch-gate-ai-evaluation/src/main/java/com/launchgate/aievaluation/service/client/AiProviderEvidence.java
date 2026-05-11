package com.launchgate.aievaluation.service.client;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AiProviderEvidence(
        @JsonProperty("path")
        String path,
        @JsonProperty("chunk_index")
        Integer chunkIndex,
        @JsonProperty("quote")
        String quote,
        @JsonProperty("why")
        String why
) {
}
