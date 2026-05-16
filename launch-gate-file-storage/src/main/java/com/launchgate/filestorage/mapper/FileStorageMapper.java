package com.launchgate.filestorage.mapper;

import com.launchgate.filestorage.dto.FileResponse;
import com.launchgate.filestorage.entity.StoredFile;
import lombok.experimental.UtilityClass;

@UtilityClass
public class FileStorageMapper {

    public FileResponse toResponse(StoredFile file) {
        return new FileResponse(file.getId(), file.getOriginalFilename(), file.getContentType(), file.getSizeBytes());
    }
}
