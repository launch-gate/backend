package com.launchgate.export.dto;

import com.launchgate.export.entity.*;

import jakarta.validation.constraints.NotNull;

public record CustomExportRequest(
        @NotNull ExportFormat format,
        String prompt
) {
}
