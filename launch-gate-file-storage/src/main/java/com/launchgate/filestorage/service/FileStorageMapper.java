package com.launchgate.filestorage.service;

import com.launchgate.filestorage.dto.FileResponse;
import com.launchgate.filestorage.entity.StoredFile;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class FileStorageMapper {

    public static FileResponse toResponse(StoredFile file) {
        return new FileResponse(file.getId(), file.getOriginalFilename(), file.getContentType(), file.getSizeBytes());
    }
}
