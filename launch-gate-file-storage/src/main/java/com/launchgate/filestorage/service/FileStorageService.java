package com.launchgate.filestorage.service;

import lombok.RequiredArgsConstructor;

import com.launchgate.filestorage.dto.*;
import com.launchgate.filestorage.entity.*;
import com.launchgate.filestorage.repository.*;

import com.launchgate.common.DomainException;
import com.launchgate.common.NotFoundException;
import com.launchgate.filestorage.config.FileStorageProperties;
import com.launchgate.identity.dto.AuthenticatedUser;
import io.minio.BucketExistsArgs;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.http.Method;
import java.time.Clock;
import java.time.Instant;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class FileStorageService {
    private final StoredFileRepository fileRepository;
    private final MinioClient minioClient;
    private final FileStorageProperties properties;
    private final Clock clock;
    @Transactional
    public FileResponse upload(AuthenticatedUser user, MultipartFile file) {
        if (file.isEmpty()) {
            throw new DomainException("empty_file", "File must not be empty");
        }
        var objectKey = user.id() + "/" + Long.toUnsignedString(java.util.concurrent.ThreadLocalRandom.current().nextLong(), 36) + "/" + file.getOriginalFilename();
        try {
            ensureBucket();
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(properties.bucket())
                    .object(objectKey)
                    .contentType(file.getContentType())
                    .stream(file.getInputStream(), file.getSize(), -1)
                    .build());
        } catch (Exception exception) {
            throw new DomainException("file_upload_failed", "Could not upload file to object storage");
        }
        var storedFile = fileRepository.save(new StoredFile(
                user.id(),
                properties.bucket(),
                objectKey,
                file.getOriginalFilename(),
                file.getContentType(),
                file.getSize(),
                Instant.now(clock)
        ));
        return FileStorageMapper.toResponse(storedFile);
    }

    @Transactional(readOnly = true)
    public DownloadUrlResponse downloadUrl(Long fileId) {
        var file = fileRepository.findById(fileId).orElseThrow(() -> new NotFoundException("File not found"));
        try {
            var url = minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
                    .method(Method.GET)
                    .bucket(file.getBucket())
                    .object(file.getObjectKey())
                    .expiry(60 * 20)
                    .build());
            return new DownloadUrlResponse(url);
        } catch (Exception exception) {
            throw new DomainException("file_url_failed", "Could not create download URL");
        }
    }

    private void ensureBucket() throws Exception {
        var exists = minioClient.bucketExists(BucketExistsArgs.builder().bucket(properties.bucket()).build());
        if (!exists) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(properties.bucket()).build());
        }
    }

}
