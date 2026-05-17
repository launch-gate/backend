package com.launchgate.filestorage.controller;

import com.launchgate.filestorage.service.FileStorageService;
import lombok.RequiredArgsConstructor;

import com.launchgate.filestorage.dto.*;
import com.launchgate.identity.dto.AuthenticatedUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * Контроллер для работы с файловым хранилищем.
 */
@RestController
@RequestMapping("/api/v1/files")
@RequiredArgsConstructor
@Tag(name = "Файловое хранилище", description = "Загрузка файлов")
public class FileStorageController {
    private final FileStorageService fileStorageService;

    @PostMapping
    @Operation(summary = "Загрузить файл и получить мета данные")
    public FileResponse upload(@AuthenticationPrincipal AuthenticatedUser user, @RequestParam("file") MultipartFile file) {
        return fileStorageService.upload(user, file);
    }

    @GetMapping("/{fileId}/download-url")
    @Operation(summary = "Получить ссылку на загруженный файл")
    public DownloadUrlResponse downloadUrl(@PathVariable Long fileId) {
        return fileStorageService.downloadUrl(fileId);
    }
}
