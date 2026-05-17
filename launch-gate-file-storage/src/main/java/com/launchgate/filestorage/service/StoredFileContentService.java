package com.launchgate.filestorage.service;

import com.launchgate.common.DomainException;
import com.launchgate.common.NotFoundException;
import com.launchgate.filestorage.dto.StoredFileContent;
import com.launchgate.filestorage.entity.StoredFile;
import com.launchgate.filestorage.repository.StoredFileRepository;
import io.minio.GetObjectArgs;
import io.minio.GetObjectResponse;
import io.minio.MinioClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Сервис загрузки файлов в MinIO.
 */
@Service
@RequiredArgsConstructor
public class StoredFileContentService {
    private final StoredFileRepository storedFileRepository;
    private final MinioClient minioClient;

    /**
     * Загрузить файл в MinIO.
     *
     * @param fileId уникальный идентификатор файла.
     * @return загруженный файл.
     */
    public StoredFileContent load(Long fileId) {
        StoredFile storedFile = storedFileRepository.findById(fileId)
                .orElseThrow(() -> new NotFoundException("Файл не найден"));

        try (GetObjectResponse objectStream = minioClient.getObject(GetObjectArgs.builder()
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
            throw new DomainException("Ошибка загрузки файла", "Не удалось прочитать содержимое файла");
        }
    }
}
