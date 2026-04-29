package com.launchgate.identity.service;

import lombok.RequiredArgsConstructor;

import com.launchgate.identity.dto.*;
import com.launchgate.identity.entity.*;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.launchgate.common.DomainException;
import com.launchgate.identity.entity.AccountType;
import com.launchgate.identity.security.JwtProperties;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Clock;
import java.time.Instant;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JwtService {
    private static final String HMAC_SHA256 = "HmacSHA256";
    private static final Base64.Encoder URL_ENCODER = Base64.getUrlEncoder().withoutPadding();
    private static final Base64.Decoder URL_DECODER = Base64.getUrlDecoder();
    private static final TypeReference<Map<String, Object>> CLAIMS_TYPE = new TypeReference<>() {
    };

    private final JwtProperties properties;
    private final ObjectMapper objectMapper;
    private final Clock clock;

    public String issue(UserAccount user, String tokenId) {
        var now = Instant.now(clock);
        var expiresAt = now.plusSeconds(properties.accessTokenTtlMinutes() * 60);
        var header = Map.of("alg", "HS256", "typ", "JWT");
        var claims = new LinkedHashMap<String, Object>();
        claims.put("iss", properties.issuer());
        claims.put("sub", user.getId().toString());
        claims.put("tid", tokenId);
        claims.put("role", user.getAccountType().name());
        claims.put("iat", now.getEpochSecond());
        claims.put("exp", expiresAt.getEpochSecond());

        var unsignedToken = encodeJson(header) + "." + encodeJson(claims);
        return unsignedToken + "." + sign(unsignedToken);
    }

    public JwtClaims verify(String authorizationHeader) {
        var token = extractBearer(authorizationHeader);
        var parts = token.split("\\.");
        if (parts.length != 3) {
            throw new DomainException("bad_token", "JWT token is invalid");
        }
        var unsignedToken = parts[0] + "." + parts[1];
        if (!constantTimeEquals(sign(unsignedToken), parts[2])) {
            throw new DomainException("bad_token", "JWT signature is invalid");
        }
        var claims = decodeJson(parts[1]);
        if (!properties.issuer().equals(claims.get("iss"))) {
            throw new DomainException("bad_token", "JWT issuer is invalid");
        }
        var expiresAt = numberClaim(claims, "exp");
        if (Instant.ofEpochSecond(expiresAt).isBefore(Instant.now(clock))) {
            throw new DomainException("expired_token", "JWT token expired");
        }
        var userId = parseLongClaim(claims, "sub");
        var tokenId = stringClaim(claims, "tid");
        var accountType = parseAccountType(claims);
        return new JwtClaims(userId, tokenId, accountType, Instant.ofEpochSecond(expiresAt));
    }

    public long expiresInSeconds() {
        return properties.accessTokenTtlMinutes() * 60;
    }

    private String extractBearer(String authorizationHeader) {
        if (authorizationHeader == null || authorizationHeader.isBlank()) {
            throw new DomainException("missing_token", "Authorization header is required");
        }
        if (!authorizationHeader.regionMatches(true, 0, "Bearer ", 0, 7)) {
            throw new DomainException("bad_token", "Authorization header must use Bearer scheme");
        }
        return authorizationHeader.substring(7).trim();
    }

    private String encodeJson(Map<String, ?> value) {
        try {
            return URL_ENCODER.encodeToString(objectMapper.writeValueAsBytes(value));
        } catch (Exception exception) {
            throw new IllegalStateException("Could not encode JWT JSON", exception);
        }
    }

    private Map<String, Object> decodeJson(String value) {
        try {
            return objectMapper.readValue(URL_DECODER.decode(value), CLAIMS_TYPE);
        } catch (Exception exception) {
            throw new DomainException("bad_token", "JWT payload is invalid");
        }
    }

    private String sign(String value) {
        try {
            var mac = Mac.getInstance(HMAC_SHA256);
            mac.init(new SecretKeySpec(properties.secret().getBytes(StandardCharsets.UTF_8), HMAC_SHA256));
            return URL_ENCODER.encodeToString(mac.doFinal(value.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception exception) {
            throw new IllegalStateException("Could not sign JWT", exception);
        }
    }

    private boolean constantTimeEquals(String left, String right) {
        return MessageDigest.isEqual(left.getBytes(StandardCharsets.UTF_8), right.getBytes(StandardCharsets.UTF_8));
    }

    private long numberClaim(Map<String, Object> claims, String name) {
        var value = claims.get(name);
        if (value instanceof Number number) {
            return number.longValue();
        }
        throw new DomainException("bad_token", "JWT claim " + name + " is invalid");
    }

    private Long parseLongClaim(Map<String, Object> claims, String name) {
        var value = stringClaim(claims, name);
        try {
            return Long.parseLong(value);
        } catch (IllegalArgumentException exception) {
            throw new DomainException("bad_token", "JWT claim " + name + " is invalid");
        }
    }

    private String stringClaim(Map<String, Object> claims, String name) {
        var value = claims.get(name);
        if (value instanceof String stringValue && !stringValue.isBlank()) {
            return stringValue;
        }
        throw new DomainException("bad_token", "JWT claim " + name + " is invalid");
    }

    private AccountType parseAccountType(Map<String, Object> claims) {
        var value = stringClaim(claims, "role");
        try {
            return AccountType.valueOf(value);
        } catch (IllegalArgumentException exception) {
            throw new DomainException("bad_token", "JWT role is invalid");
        }
    }
}
