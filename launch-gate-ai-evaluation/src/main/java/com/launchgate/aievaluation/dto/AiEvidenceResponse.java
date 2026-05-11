package com.launchgate.aievaluation.dto;

public record AiEvidenceResponse(
        String path,
        Integer chunkIndex,
        String quote,
        String why
) {
}
