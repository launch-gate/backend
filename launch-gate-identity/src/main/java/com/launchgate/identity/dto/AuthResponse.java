package com.launchgate.identity.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Authentication response with access token")
public record AuthResponse(
        @Schema(description = "JWT access token")
        String accessToken,
        @Schema(description = "Token type", example = "Bearer")
        String tokenType,
        @Schema(description = "Access token TTL in seconds", example = "86400")
        long expiresInSeconds,
        UserProfileResponse user
) {
}
