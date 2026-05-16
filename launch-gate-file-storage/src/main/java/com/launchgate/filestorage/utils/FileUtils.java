package com.launchgate.filestorage.utils;

import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;

import java.util.regex.Pattern;

/**
 * Утилитный класс для работы с файлами.
 */
@UtilityClass
public class FileUtils {

    /**
     * Время истечения ссылки для получния файла в секундах.
     */
    public Integer FILE_URL_EXPIRATION_SECONDS  = 604800;

    /**
     * Сгенерировать ключ файла в MinIO.
     * @param userId идентификатор пользователя.
     * @param fileName имя файла.
     * @return ключ файла в MinIO
     */
    public String generateObjectKey(Long userId, String fileName) {

        String randomHash = Long.toUnsignedString(java.util.concurrent.ThreadLocalRandom.current().nextLong(), 36);
        return "%d/%s/%s".formatted(userId, randomHash, fileName);
    }

    /**
     * Получить публичную ссылку.
     * @param internalUrl внутренний url.
     * @param minIoPublicEndpoint публичная url MinIO.
     * @param minIoEndpoint внутренний url MinIO.
     * @return публичная ссылка
     */
    public String getPublicUrl(String internalUrl, String minIoPublicEndpoint, String minIoEndpoint) {
        String publicEndpoint = normalizeEndpoint(minIoPublicEndpoint);
        String internalEndpoint = normalizeEndpoint(minIoEndpoint);

        boolean urlsIsValid = StringUtils.isBlank(publicEndpoint) || StringUtils.isBlank(internalEndpoint) || publicEndpoint.equals(internalEndpoint);
        if (urlsIsValid) {
            return internalUrl;
        }
        return internalUrl.replaceFirst(Pattern.quote(internalEndpoint), publicEndpoint);
    }

    /**
     * Нормализует URL-адрес эндпоинта.
     * @param endpoint эндпоинт.
     * @return нормализованный url
     */
    private String normalizeEndpoint(String endpoint) {
        if (StringUtils.isBlank(endpoint)) {
            return null;
        }
        return endpoint.endsWith("/") ? endpoint.substring(0, endpoint.length() - 1) : endpoint;
    }
}
