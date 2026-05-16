package com.launchgate.filestorage.config;

import com.launchgate.common.LaunchGateException;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Конфигурация MinIO.
 */
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
            throw new LaunchGateException("Не удалось инициализировать бакет: " + properties.bucket(), exception);
        }

        return minioClient;
    }
}
