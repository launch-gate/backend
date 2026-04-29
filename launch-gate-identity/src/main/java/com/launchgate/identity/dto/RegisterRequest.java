package com.launchgate.identity.dto;

import com.launchgate.identity.entity.*;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

@Schema(description = "User registration payload")
public record RegisterRequest(
        @Schema(description = "Unique email", example = "participant@launchgate.local")
        @Email @NotBlank String email,
        @Schema(description = "Password from 6 to 120 characters", example = "secret123")
        @NotBlank @Size(min = 6, max = 120) String password,
        @Schema(description = "Base account type", example = "ORGANIZER")
        @NotNull AccountType accountType,
        @Schema(description = "Full public name", example = "Ivan Petrov")
        String fullName,
        @Schema(description = "Short nickname", example = "ivanp")
        String nickname,
        @Schema(description = "Short user bio", example = "Product manager and startup mentor")
        String bio,
        List<@Valid ContactRequest> contacts
) {
}
