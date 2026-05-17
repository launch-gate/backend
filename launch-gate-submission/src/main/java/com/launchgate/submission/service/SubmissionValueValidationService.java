package com.launchgate.submission.service;

import com.launchgate.common.LaunchGateException;
import com.launchgate.contest.entity.SubmissionField;
import com.launchgate.contest.entity.SubmissionFieldFileFormat;
import com.launchgate.contest.service.ContestReaderService;
import com.launchgate.filestorage.entity.StoredFile;
import com.launchgate.filestorage.repository.StoredFileRepository;
import com.launchgate.submission.dto.ValueRequest;
import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.launchgate.submission.repository.SubmissionValueRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

/**
 * Сервис реализующий валидацию значений формы.
 */
@Service
@RequiredArgsConstructor
public class SubmissionValueValidationService {
    private final StoredFileRepository storedFileRepository;
    private final SubmissionValueRepository valueRepository;
    private final ContestReaderService contestReaderService;

    private final static Long MB_TO_BYTE = 1024L * 1024L;

    /**
     * Валидирует и нормализует текстовое поле формы
     * @param field поле
     * @param request содержимое поля.
     * @return поле
     */
    public String validateAndNormalizeValueText(SubmissionField field, ValueRequest request) {
        return switch (field.getType()) {
            case TEXT -> requireText(request.valueText());
            case LINK -> requireUrl(request.valueText(), "link");
            case GITHUB_REPOSITORY -> requireGithubRepositoryUrl(request.valueText());
            case NUMBER -> requireNumber(request.valueText());
            case FILE, FILES, PHOTO, VIDEO -> requireNoTextValue(request.valueText());
        };
    }

    /**
     * Валидирует и нормализует поле формы содержащие файлы.
     * @param field поле
     * @param request содержимое поля.
     * @return поле
     */
    public String validateAndNormalizeFileIds(SubmissionField field, ValueRequest request) {
        return switch (field.getType()) {
            case TEXT, LINK, GITHUB_REPOSITORY, NUMBER -> requireNoFiles(request.fileIds());
            case FILE, PHOTO, VIDEO -> validateFiles(field, request.fileIds(), false);
            case FILES -> validateFiles(field, request.fileIds(), true);
        };
    }

    /**
     * Валидирует обязательные поля формы.
     * @param stageId идентфикатор стадии
     * @param submissionId идентификатор формы.
     */
    public void validateRequiredFields(Long stageId, Long submissionId) {

        Set<Long> filledFields = valueRepository.findAllBySubmission_Id(submissionId).stream()
                .filter(value -> (Objects.nonNull(value.getValueText()) && !value.getValueText().isBlank())
                        || (Objects.nonNull(value.getFileIds()) && !value.getFileIds().isBlank()))
                .map(value -> value.getField().getId())
                .collect(Collectors.toSet());

        List<String> missing = contestReaderService.getSubmissionFields(stageId).stream()
                .filter(SubmissionField::isRequired)
                .filter(field -> !filledFields.contains(field.getId()))
                .map(SubmissionField::getTitle)
                .toList();

        if (!missing.isEmpty()) {
            throw new LaunchGateException("Пропущено необходимое поле: " + String.join(", ", missing));
        }
    }

    private String requireText(String value) {
        if (StringUtils.isBlank(value)) {
            throw new LaunchGateException("Текстовое поле не должно быть пустым");
        }
        return value;
    }

    private String requireUrl(String value, String label) {
        if (StringUtils.isBlank(value)) {
            throw new LaunchGateException("Поле URL требует ввода ссылки");
        }

        try {
            URI uri = URI.create(value.trim());
            if (Objects.isNull(uri.getScheme()) || Objects.isNull(uri.getHost())) {
                throw new LaunchGateException("Ошибка во время создания url");
            }
            return value.trim();
        } catch (Exception exception) {
            throw new LaunchGateException("Поле требует корректный URL-адрес в %s".formatted(label));
        }
    }

    private String requireGithubRepositoryUrl(String value) {
        String normalizedValue = requireUrl(value, "GitHub repository");

        if (!normalizedValue.matches("^https://github\\.com/[^/]+/[^/]+/?$")) {
            throw new LaunchGateException("Поле требует корректный URL-адрес репозитория GitHub");
        }

        return normalizedValue;
    }

    private String requireNumber(String value) {
        if (StringUtils.isBlank(value)) {
            throw new LaunchGateException("Числовое поле должно быть заполнено");
        }
        try {
            new java.math.BigDecimal(value.trim());
            return value.trim();
        } catch (Exception exception) {
            throw new LaunchGateException("Поле должно содержать числовое значение");
        }
    }

    private String requireNoTextValue(String value) {
        if (StringUtils.isNotBlank(value)) {
            throw new LaunchGateException("Тип поля не принимает текстовое содержимое");
        }
        return null;
    }

    private String requireNoFiles(String fileIds) {
        if (StringUtils.isNotBlank(fileIds)) {
            throw new LaunchGateException("Тип поля не принимает файлы");
        }
        return null;
    }

    private String validateFiles(SubmissionField field, String fileIds, boolean multipleAllowed) {

        if (StringUtils.isBlank(fileIds)) {
            throw new LaunchGateException("Поле загрузки требует прикрепления файлов");
        }
        List<Long> parsedFileIds;

        try {
            parsedFileIds = Arrays.stream(fileIds.split(","))
                    .map(String::trim)
                    .filter(value -> !value.isBlank())
                    .map(Long::parseLong)
                    .distinct()
                    .toList();

        } catch (Exception exception) {
            throw new LaunchGateException("Идентификаторы файлов должны содержать корректные числовые указатели");
        }

        if (parsedFileIds.isEmpty()) {
            throw new LaunchGateException("Поле загрузки требует прикрепления файлов");
        }

        if (!multipleAllowed && parsedFileIds.size() > 1) {
            throw new LaunchGateException("Поле принимает только один файл");
        }

        List<StoredFile> files = storedFileRepository.findAllById(parsedFileIds);

        if (files.size() != parsedFileIds.size()) {
            throw new LaunchGateException("Один или несколько загруженных файлов не найдены");
        }

        validateFilesAgainstField(field, files);

        return parsedFileIds.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));
    }

    private void validateFilesAgainstField(SubmissionField field, List<StoredFile> files) {
        List<SubmissionFieldFileFormat> allowedFormats = field.getFileFormats();

        if (allowedFormats.isEmpty()) {
            throw new LaunchGateException("Не определены допустимые форматы поля загрузки");
        }

        Long maxSizeBytes = Optional.ofNullable(field.getMaxFileSizeMb())
                .map(mb -> mb * MB_TO_BYTE)
                .orElse(null);

        files.forEach(file -> validFile(allowedFormats, file, maxSizeBytes));
    }

    private void validFile(List<SubmissionFieldFileFormat> allowedFormats, StoredFile file, Long maxSizeBytes) {
        boolean matchesFormat = allowedFormats.stream()
                .anyMatch(format -> format.matches(file.getOriginalFilename(), file.getContentType()));

        if (!matchesFormat) {
            throw new LaunchGateException("Формат загруженного файла не поддерживается этим полем");
        }
        if (Objects.nonNull(maxSizeBytes) && file.getSizeBytes() > maxSizeBytes) {
            throw new LaunchGateException("Размер загруженного файла превышает установленный лимит");
        }
    }
}
