package com.launchgate.identity.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "User login payload")
public record LoginRequest(
        @Schema(description = "Login email", example = "organizer@launchgate.local")
        @Email @NotBlank String email,
        @Schema(description = "User password", example = "secret123")
        @NotBlank String password
) {
}
