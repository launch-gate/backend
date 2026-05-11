package com.launchgate.aievaluation.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "launch-gate.ai-evaluation")
public record AiEvaluationProperties(
        String repositoryReviewUrl,
        String textReviewUrl
) {
}
