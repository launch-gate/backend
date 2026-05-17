package com.launchgate.aievaluation.service.strategy;

import com.launchgate.aievaluation.entity.AiFieldReviewStatus;
import com.launchgate.aievaluation.entity.AiReviewSourceType;
import com.launchgate.aievaluation.service.extractor.FileTextExtractorStrategy;
import com.launchgate.aievaluation.service.model.AiPayloadKind;
import com.launchgate.aievaluation.service.model.PreparedFieldPayload;
import com.launchgate.contest.entity.FieldCriterion;
import com.launchgate.contest.enums.FieldType;
import com.launchgate.contest.entity.SubmissionField;
import com.launchgate.filestorage.service.StoredFileContentService;
import com.launchgate.submission.entity.SubmissionValue;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TextAiFieldPayloadStrategy implements AiFieldPayloadStrategy {
    private final StoredFileContentService storedFileContentService;
    private final List<FileTextExtractorStrategy> extractorStrategies;

    @Override
    public boolean supports(SubmissionField field, SubmissionValue value) {
        return field.getType() == FieldType.TEXT
                || field.getType() == FieldType.FILE
                || field.getType() == FieldType.FILES;
    }

    @Override
    public PreparedFieldPayload prepare(SubmissionField field, SubmissionValue value) {
        var criteria = orderedCriteria(field);
        if (field.getType() == FieldType.TEXT) {
            if (value == null || value.getValueText() == null || value.getValueText().isBlank()) {
                return skipped(field, value, criteria, "No text content was submitted for this field");
            }
            return new PreparedFieldPayload(
                    field,
                    field.getOrder(),
                    field.getTitle(),
                    field.getType(),
                    value,
                    null,
                    AiReviewSourceType.TEXT_CONTENT,
                    null,
                    AiPayloadKind.TEXT,
                    value.getValueText(),
                    criteria
            );
        }
        if (value == null || value.getFileIds() == null || value.getFileIds().isBlank()) {
            return skipped(field, value, criteria, "No files were submitted for this field");
        }
        var extractedParts = new ArrayList<String>();
        var unsupportedFiles = new ArrayList<String>();
        for (var fileId : parseFileIds(value.getFileIds())) {
            var file = storedFileContentService.load(fileId);
            var extractor = extractorStrategies.stream()
                    .filter(strategy -> strategy.supports(file))
                    .findFirst()
                    .orElse(null);
            if (extractor == null) {
                unsupportedFiles.add(file.originalFilename());
                continue;
            }
            extractedParts.add("File: " + file.originalFilename() + "\n" + extractor.extract(file));
        }
        if (extractedParts.isEmpty()) {
            return new PreparedFieldPayload(
                    field,
                    field.getOrder(),
                    field.getTitle(),
                    field.getType(),
                    value,
                    AiFieldReviewStatus.UNSUPPORTED_FORMAT,
                    AiReviewSourceType.TEXT_FILE,
                    unsupportedFiles.isEmpty()
                            ? "There are no text-readable files in this field yet"
                            : "Unsupported file formats: " + String.join(", ", unsupportedFiles),
                    null,
                    null,
                    criteria
            );
        }
        var message = unsupportedFiles.isEmpty()
                ? null
                : "Some files were skipped because text extraction is not supported yet: " + String.join(", ", unsupportedFiles);
        return new PreparedFieldPayload(
                field,
                field.getOrder(),
                field.getTitle(),
                field.getType(),
                value,
                null,
                AiReviewSourceType.TEXT_FILE,
                message,
                AiPayloadKind.TEXT,
                String.join("\n\n---\n\n", extractedParts),
                criteria
        );
    }

    private PreparedFieldPayload skipped(
            SubmissionField field,
            SubmissionValue value,
            List<FieldCriterion> criteria,
            String message
    ) {
        return new PreparedFieldPayload(
                field,
                field.getOrder(),
                field.getTitle(),
                field.getType(),
                value,
                AiFieldReviewStatus.SKIPPED_NO_DATA,
                AiReviewSourceType.TEXT_CONTENT,
                message,
                null,
                null,
                criteria
        );
    }

    private List<Long> parseFileIds(String rawFileIds) {
        return java.util.Arrays.stream(rawFileIds.split(","))
                .map(String::trim)
                .filter(value -> !value.isBlank())
                .map(Long::parseLong)
                .toList();
    }

    private List<FieldCriterion> orderedCriteria(SubmissionField field) {
        return field.getCriteria().stream()
                .sorted(Comparator.comparing(FieldCriterion::getOrder))
                .toList();
    }
}
