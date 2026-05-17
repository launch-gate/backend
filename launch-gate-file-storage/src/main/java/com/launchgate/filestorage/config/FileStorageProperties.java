package com.launchgate.filestorage.config;


import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Конфигурация для интеграции с MinIO
 *
 * @param endpoint       внутренний URL-адрес сервиса хранилища для серверных запросов.
 * @param publicEndpoint внешний URL-адрес для доступа к файлам.
 * @param accessKey      идентификатор ключа доступа.
 * @param secretKey      секретный ключ для подписи запросов.
 * @param bucket         корзина для хранения файлов.
 */
@ConfigurationProperties(prefix = "launch-gate.files")
public record FileStorageProperties(
        String endpoint,
        String publicEndpoint,
        String accessKey,
        String secretKey,
        String bucket
) {
}
