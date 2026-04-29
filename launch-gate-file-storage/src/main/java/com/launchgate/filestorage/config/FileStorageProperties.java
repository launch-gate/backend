package com.launchgate.filestorage.config;


import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "launch-gate.files")
public record FileStorageProperties(
        String endpoint,
        String accessKey,
        String secretKey,
        String bucket
) {
}
