package com.launchgate.identity.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "launch-gate.jwt")
public record JwtProperties(
        String secret,
        String issuer,
        long accessTokenTtlMinutes
) {
}
