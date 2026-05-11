package com.launchgate.submission.service;

import com.launchgate.common.DomainException;
import com.launchgate.contest.entity.FieldType;
import com.launchgate.contest.entity.SubmissionField;
import com.launchgate.filestorage.entity.StoredFile;
import com.launchgate.filestorage.repository.StoredFileRepository;
import com.launchgate.submission.dto.ValueRequest;
import java.net.URI;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SubmissionValueValidationService {
    private final StoredFileRepository storedFileRepository;

    public String validateAndNormalizeValueText(SubmissionField field, ValueRequest request) {
        return switch (field.getType()) {
            case TEXT -> requireText(request.valueText());
            case LINK -> requireUrl(request.valueText(), "link");
            case GITHUB_REPOSITORY -> requireGithubRepositoryUrl(request.valueText());
            case NUMBER -> requireNumber(request.valueText());
            case FILE, FILES, PHOTO, VIDEO -> requireNoTextValue(request.valueText());
        };
    }

    public String validateAndNormalizeFileIds(SubmissionField field, ValueRequest request) {
        return switch (field.getType()) {
            case TEXT, LINK, GITHUB_REPOSITORY, NUMBER -> requireNoFiles(request.fileIds());
            case FILE, PHOTO, VIDEO -> validateFiles(field, request.fileIds(), false);
            case FILES -> validateFiles(field, request.fileIds(), true);
        };
    }

    private String requireText(String value) {
        if (value == null || value.isBlank()) {
            throw new DomainException("value_text_required", "Text field requires text content");
        }
        return value;
    }

    private String requireUrl(String value, String label) {
        if (value == null || value.isBlank()) {
            throw new DomainException("value_url_required", "URL field requires a link");
        }
        try {
            var uri = URI.create(value.trim());
            if (uri.getScheme() == null || uri.getHost() == null) {
                throw new IllegalArgumentException();
            }
            return value.trim();
        } catch (Exception exception) {
            throw new DomainException("value_url_invalid", "Field requires valid " + label + " URL");
        }
    }

    private String requireGithubRepositoryUrl(String value) {
        var normalizedValue = requireUrl(value, "GitHub repository");
        if (!normalizedValue.matches("^https://github\\.com/[^/]+/[^/]+/?$")) {
            throw new DomainException("value_github_invalid", "Field requires valid GitHub repository URL");
        }
        return normalizedValue;
    }

    private String requireNumber(String value) {
        if (value == null || value.isBlank()) {
            throw new DomainException("value_number_required", "Number field requires value");
        }
        try {
            new java.math.BigDecimal(value.trim());
            return value.trim();
        } catch (Exception exception) {
            throw new DomainException("value_number_invalid", "Field requires numeric value");
        }
    }

    private String requireNoTextValue(String value) {
        if (value != null && !value.isBlank()) {
            throw new DomainException("value_text_not_allowed", "This field type does not accept text content");
        }
        return null;
    }

    private String requireNoFiles(String fileIds) {
        if (fileIds != null && !fileIds.isBlank()) {
            throw new DomainException("value_files_not_allowed", "This field type does not accept files");
        }
        return null;
    }

    private String validateFiles(SubmissionField field, String fileIds, boolean multipleAllowed) {
        if (fileIds == null || fileIds.isBlank()) {
            throw new DomainException("value_files_required", "Upload field requires file attachments");
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
            throw new DomainException("value_file_ids_invalid", "File ids must contain valid numeric identifiers");
        }
        if (parsedFileIds.isEmpty()) {
            throw new DomainException("value_files_required", "Upload field requires file attachments");
        }
        if (!multipleAllowed && parsedFileIds.size() > 1) {
            throw new DomainException("value_files_too_many", "This field accepts only one file");
        }
        var files = storedFileRepository.findAllById(parsedFileIds);
        if (files.size() != parsedFileIds.size()) {
            throw new DomainException("value_file_missing", "One or more uploaded files were not found");
        }
        validateFilesAgainstField(field, files);
        return parsedFileIds.stream().map(String::valueOf).collect(java.util.stream.Collectors.joining(","));
    }

    private void validateFilesAgainstField(SubmissionField field, List<StoredFile> files) {
        var allowedFormats = field.getFileFormats();
        if (allowedFormats.isEmpty()) {
            throw new DomainException("field_file_formats_missing", "Upload field does not define allowed formats");
        }
        var maxSizeBytes = field.getMaxFileSizeMb() == null ? null : field.getMaxFileSizeMb() * 1024L * 1024L;
        for (var file : files) {
            var matchesFormat = allowedFormats.stream()
                    .anyMatch(format -> format.matches(file.getOriginalFilename(), file.getContentType()));
            if (!matchesFormat) {
                throw new DomainException("value_file_format_invalid", "Uploaded file format is not allowed for this field");
            }
            if (maxSizeBytes != null && file.getSizeBytes() > maxSizeBytes) {
                throw new DomainException("value_file_size_invalid", "Uploaded file exceeds configured size limit");
            }
        }
    }

}
