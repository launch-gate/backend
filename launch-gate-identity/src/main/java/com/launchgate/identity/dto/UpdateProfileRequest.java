package com.launchgate.identity.dto;

import jakarta.validation.Valid;

import java.util.List;

public record UpdateProfileRequest(
        String fullName,
        String nickname,
        String bio,
        List<@Valid ContactRequest> contacts
) {
}
