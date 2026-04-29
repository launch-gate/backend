package com.launchgate.identity.dto;

import com.launchgate.identity.entity.AccountType;
import java.time.Instant;

public record JwtClaims(
        Long userId,
        String tokenId,
        AccountType accountType,
        Instant expiresAt
) {
}
