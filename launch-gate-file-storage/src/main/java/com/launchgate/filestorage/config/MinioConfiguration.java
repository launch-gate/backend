package com.launchgate.filestorage.config;

import com.launchgate.common.LaunchGateException;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Конфигурация MinIO.
 */
@Slf4j
@Configuration
@EnableConfigurationProperties(FileStorageProperties.class)
public class MinioConfiguration {

    /**
     * Конфигурация MinIO клиента.
     */
    @Bean
    public MinioClient minioClient(FileStorageProperties properties) {
        MinioClient minioClient = MinioClient.builder()
                .endpoint(properties.endpoint())
                .credentials(properties.accessKey(), properties.secretKey())
                .build();

        try {
            boolean exists = minioClient.bucketExists(BucketExistsArgs.builder()
                    .bucket(properties.bucket())
                    .build());

            if (!exists) {
                minioClient.makeBucket(MakeBucketArgs.builder()
                        .bucket(properties.bucket())
                        .build());
            }
        } catch (Exception exception) {
            log.error("Не удалось инициализировать бакет: {}", properties.bucket(), exception);
            throw new LaunchGateException("Не удалось инициализировать бакет: %s".formatted(properties.bucket()));
        }

        return minioClient;
    }
}
