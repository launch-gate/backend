package com.launchgate.filestorage.service;

import com.launchgate.filestorage.mapper.FileStorageMapper;
import com.launchgate.filestorage.utils.FileUtils;
import com.launchgate.identity.entity.UserAccount;
import lombok.RequiredArgsConstructor;

import com.launchgate.filestorage.dto.*;
import com.launchgate.filestorage.entity.*;
import com.launchgate.filestorage.repository.*;

import com.launchgate.common.DomainException;
import com.launchgate.common.NotFoundException;
import com.launchgate.filestorage.config.FileStorageProperties;
import com.launchgate.identity.dto.AuthenticatedUser;
import com.launchgate.identity.repository.UserAccountRepository;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.http.Method;
import java.time.Clock;
import java.time.Instant;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * Сервис реализующий загрузку файлов.
 */
@Service
@RequiredArgsConstructor
public class FileStorageService {
    private final StoredFileRepository fileRepository;
    private final UserAccountRepository userAccountRepository;
    private final MinioClient minioClient;
    private final FileStorageProperties properties;
    private final Clock clock;

    /**
     * Загрузить файл.
     * @param user пользователь.
     * @param file файл.
     * @return информация о загруженном файле.
     */
    public FileResponse upload(AuthenticatedUser user, MultipartFile file) {
        if (file.isEmpty()) {
            throw new DomainException("Ошибка загрузки файла", "Файл отсутствует");
        }

        String objectKey = FileUtils.generateObjectKey(user.id(), file.getOriginalFilename());

        UserAccount owner = userAccountRepository.findById(user.id())
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        try {
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(properties.bucket())
                    .object(objectKey)
                    .contentType(file.getContentType())
                    .stream(file.getInputStream(), file.getSize(), -1)
                    .build());
        } catch (Exception exception) {
            throw new DomainException("Ошибка загрузки файла", "Не удалось загрузить файл в объектное хранилище");
        }

        StoredFile storedFile = fileRepository.save(new StoredFile(
                owner,
                properties.bucket(),
                objectKey,
                file.getOriginalFilename(),
                file.getContentType(),
                file.getSize(),
                Instant.now(clock)
        ));

        return FileStorageMapper.toResponse(storedFile);
    }

    /**
     * Получить ссылку на файл.
     * @param fileId идентификатор файла
     * @return ссылка для получение файла.
     */
    public DownloadUrlResponse downloadUrl(Long fileId) {
        StoredFile file = fileRepository.findById(fileId)
                .orElseThrow(() -> new NotFoundException("Файл не найден"));
        try {
            String url = minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
                    .method(Method.GET)
                    .bucket(file.getBucket())
                    .object(file.getObjectKey())
                    .expiry(FileUtils.FILE_URL_EXPIRATION_SECONDS)
                    .build());

            return new DownloadUrlResponse(FileUtils.getPublicUrl(url, properties.publicEndpoint(), properties.endpoint()));
        } catch (Exception exception) {
            throw new DomainException("Ошибка формирования ссылки на файл", "Не удалось создать ссылку для скачивания");
        }
    }
}
