package com.launchgate.contest.dto;

import com.launchgate.contest.entity.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

public record OrganizerResponse(
        Long id,
        Long userId,
        ContestRole role
) {
}
