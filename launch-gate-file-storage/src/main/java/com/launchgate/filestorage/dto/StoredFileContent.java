package com.launchgate.filestorage.dto;

/**
 * Модель содержимого файла.
 * @param id уникальный идентификатор.
 * @param originalFilename имя файла.
 * @param contentType MIME тип файла.
 * @param bytes файл.
 */
public record StoredFileContent(
        Long id,
        String originalFilename,
        String contentType,
        byte[] bytes
) {
}
