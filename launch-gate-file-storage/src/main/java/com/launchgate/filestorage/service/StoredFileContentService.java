package com.launchgate.filestorage.service;

import com.launchgate.common.DomainException;
import com.launchgate.common.NotFoundException;
import com.launchgate.filestorage.repository.StoredFileRepository;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StoredFileContentService {
    private final StoredFileRepository storedFileRepository;
    private final MinioClient minioClient;

    @Transactional(readOnly = true)
    public StoredFileContent load(Long fileId) {
        var storedFile = storedFileRepository.findById(fileId)
                .orElseThrow(() -> new NotFoundException("File not found"));
        try (var objectStream = minioClient.getObject(GetObjectArgs.builder()
                .bucket(storedFile.getBucket())
                .object(storedFile.getObjectKey())
                .build())) {
            return new StoredFileContent(
                    storedFile.getId(),
                    storedFile.getOriginalFilename(),
                    storedFile.getContentType(),
                    objectStream.readAllBytes()
            );
        } catch (Exception exception) {
            throw new DomainException("file_read_failed", "Could not read file content");
        }
    }
}
