package com.launchgate.filestorage.service;

import com.launchgate.filestorage.dto.DownloadUrlResponse;
import com.launchgate.filestorage.dto.FileResponse;
import com.launchgate.identity.dto.AuthenticatedUser;
import org.springframework.web.multipart.MultipartFile;

/**
 * Сервис реализующий загрузку файлов.
 */
public interface FileStorageService {

    /**
     * Загрузить файл.
     *
     * @param user пользователь.
     * @param file файл.
     * @return информация о загруженном файле.
     */
    FileResponse upload(AuthenticatedUser user, MultipartFile file);

    /**
     * Получить ссылку на файл.
     *
     * @param fileId идентификатор файла
     * @return ссылка для получение файла.
     */
    DownloadUrlResponse downloadUrl(Long fileId);
}