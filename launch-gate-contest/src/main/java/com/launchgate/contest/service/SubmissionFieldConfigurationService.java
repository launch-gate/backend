package com.launchgate.contest.service;

import com.launchgate.common.LaunchGateException;
import com.launchgate.contest.dto.SubmissionFieldRequest;
import com.launchgate.contest.enums.FieldFileFormatCategory;
import com.launchgate.contest.enums.SubmissionFieldFileFormat;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;


import java.util.List;

/**
 * Сервис для валидации полей формы.
 */
@Service
public class SubmissionFieldConfigurationService {

    /**
     * Валидировать поле формы.
     * @param request поле
     */
    public void validate(SubmissionFieldRequest request) {
        validateDuplicates(request);
        switch (request.type()) {
            case TEXT, LINK, GITHUB_REPOSITORY, NUMBER -> validateNonFileField(request);
            case FILE, FILES -> validateGenericFileField(request);
            case PHOTO -> validateMediaField(request, FieldFileFormatCategory.IMAGE);
            case VIDEO -> validateMediaField(request, FieldFileFormatCategory.VIDEO);
        }
    }

    /**
     * Получить все форматы.
     * @return список форматов.
     */
    public List<SubmissionFieldFileFormat> allFormats() {
        return List.of(SubmissionFieldFileFormat.values());
    }

    private void validateNonFileField(SubmissionFieldRequest request) {
        if (request.fileFormats() != null && !request.fileFormats().isEmpty()) {
            throw new LaunchGateException("Этот тип поля не поддерживает ограничение на размер файла");
        }
        if (request.maxFileSizeMb() != null) {
            throw new LaunchGateException("Этот тип поля не поддерживает ограничение на размер файла");
        }
    }

    private void validateGenericFileField(SubmissionFieldRequest request) {
        if (CollectionUtils.isEmpty(request.fileFormats())) {
            throw new LaunchGateException("Поле загрузки должно определять как минимум один формат файла");
        }
        validateMaxFileSize(request);
    }

    private void validateMediaField(SubmissionFieldRequest request, FieldFileFormatCategory expectedCategory) {
        validateGenericFileField(request);

        List<SubmissionFieldFileFormat> invalidFormats = request.fileFormats().stream()
                .filter(format -> format.category() != expectedCategory)
                .toList();

        if (CollectionUtils.isNotEmpty(invalidFormats)) {
            throw new LaunchGateException("Этот тип поля поддерживает только формат %s".formatted(expectedCategory.name().toLowerCase()));
        }
    }

    private void validateMaxFileSize(SubmissionFieldRequest request) {
        if (request.maxFileSizeMb() == null || request.maxFileSizeMb() <= 0) {
            throw new LaunchGateException("Поле загрузки должно определять положительный максимальный размер файла");
        }
    }

    private void validateDuplicates(SubmissionFieldRequest request) {
        if (CollectionUtils.isEmpty(request.fileFormats())) {
            return;
        }

        long uniqueCount = request.fileFormats().stream()
                .distinct()
                .count();

        if (uniqueCount != request.fileFormats().size()) {
            throw new LaunchGateException("Поле загрузки не должно содержать дубликаты форматов");
        }
    }
}
