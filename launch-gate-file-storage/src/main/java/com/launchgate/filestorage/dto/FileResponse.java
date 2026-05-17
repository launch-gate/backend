package com.launchgate.filestorage.dto;

/**
 * Информация о загруженном файле.
 *
 * @param id               Уникальный идентификатор.
 * @param originalFilename имя файла.
 * @param contentType      MIME тип файла.
 * @param sizeBytes        размер файла в байтах.
 */
public record FileResponse(
        Long id,
        String originalFilename,
        String contentType,
        long sizeBytes
) {
}
