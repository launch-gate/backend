package com.launchgate.identity.dto;

import com.launchgate.identity.entity.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

public record ContactRequest(
        @NotNull ContactType type,
        @NotBlank String value,
        boolean primaryContact
) {
}
