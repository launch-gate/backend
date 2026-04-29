package com.launchgate.app;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "launch-gate.cors")
public record AppCorsProperties(
        String allowedOriginPatterns
) {
}
