package com.launchgate.filestorage.service;

public record StoredFileContent(
        Long id,
        String originalFilename,
        String contentType,
        byte[] bytes
) {
}
