package com.launchgate.filestorage.dto;


public record FileResponse(
        Long id,
        String originalFilename,
        String contentType,
        long sizeBytes
) {
}
