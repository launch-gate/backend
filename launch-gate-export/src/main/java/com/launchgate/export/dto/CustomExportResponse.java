package com.launchgate.export.dto;

/**
 * Результат настраиваемой выгрузки.
 *
 * @param jobId идентификатор выгрузки
 * @param preview предварительный реузьтат
 */
public record CustomExportResponse(
        Long jobId,
        String preview
) {
}
