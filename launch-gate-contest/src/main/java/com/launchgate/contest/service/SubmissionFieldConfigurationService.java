package com.launchgate.contest.service;

import com.launchgate.common.DomainException;
import com.launchgate.contest.dto.SubmissionFieldRequest;
import com.launchgate.contest.entity.FieldFileFormatCategory;
import com.launchgate.contest.entity.FieldType;
import com.launchgate.contest.entity.SubmissionFieldFileFormat;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SubmissionFieldConfigurationService {

    public void validate(SubmissionFieldRequest request) {
        validateDuplicates(request);
        switch (request.type()) {
            case TEXT, LINK, GITHUB_REPOSITORY, NUMBER -> validateNonFileField(request);
            case FILE, FILES -> validateGenericFileField(request);
            case PHOTO -> validateMediaField(request, FieldFileFormatCategory.IMAGE);
            case VIDEO -> validateMediaField(request, FieldFileFormatCategory.VIDEO);
        }
    }

    public List<SubmissionFieldFileFormat> allFormats() {
        return List.of(SubmissionFieldFileFormat.values());
    }

    private void validateNonFileField(SubmissionFieldRequest request) {
        if (request.fileFormats() != null && !request.fileFormats().isEmpty()) {
            throw new DomainException("field_file_formats_not_allowed", "This field type does not support file formats");
        }
        if (request.maxFileSizeMb() != null) {
            throw new DomainException("field_file_size_not_allowed", "This field type does not support file size limit");
        }
    }

    private void validateGenericFileField(SubmissionFieldRequest request) {
        if (request.fileFormats() == null || request.fileFormats().isEmpty()) {
            throw new DomainException("field_file_formats_required", "Upload field must define at least one file format");
        }
        validateMaxFileSize(request);
    }

    private void validateMediaField(SubmissionFieldRequest request, FieldFileFormatCategory expectedCategory) {
        validateGenericFileField(request);
        var invalidFormats = request.fileFormats().stream()
                .filter(format -> format.category() != expectedCategory)
                .toList();
        if (!invalidFormats.isEmpty()) {
            throw new DomainException(
                    "field_file_formats_invalid",
                    "This field type supports only " + expectedCategory.name().toLowerCase() + " formats"
            );
        }
    }

    private void validateMaxFileSize(SubmissionFieldRequest request) {
        if (request.maxFileSizeMb() == null || request.maxFileSizeMb() <= 0) {
            throw new DomainException("field_file_size_required", "Upload field must define positive max file size");
        }
    }

    private void validateDuplicates(SubmissionFieldRequest request) {
        if (request.fileFormats() == null || request.fileFormats().isEmpty()) {
            return;
        }
        var uniqueCount = request.fileFormats().stream().distinct().count();
        if (uniqueCount != request.fileFormats().size()) {
            throw new DomainException("field_file_formats_duplicate", "Upload field must not contain duplicate formats");
        }
    }
}
