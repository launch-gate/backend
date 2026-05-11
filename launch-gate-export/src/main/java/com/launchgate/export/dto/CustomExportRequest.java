package com.launchgate.export.dto;

import com.launchgate.export.entity.*;

import jakarta.validation.constraints.NotNull;

/**
 * Запрос на создание настроиваемой выгрузки.
 *
 * @param format формат выгрузки
 * @param prompt настройки выгрузки
 */
public record CustomExportRequest(
        @NotNull ExportFormat format,
        String prompt
) {
}
