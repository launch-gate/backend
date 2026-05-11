package com.launchgate.contest.utils.field;

import com.launchgate.contest.dto.FieldCriterionResponse;
import com.launchgate.contest.dto.FieldParticipantResponse;
import com.launchgate.contest.dto.FieldResponse;
import com.launchgate.contest.entity.FieldCriterion;
import com.launchgate.contest.entity.SubmissionField;
import java.util.Comparator;
import java.util.List;
import lombok.experimental.UtilityClass;

@UtilityClass
public class SubmissionFieldMapper {

    public static FieldResponse toFieldResponse(SubmissionField field) {
        return new FieldResponse(
                field.getId(),
                field.getOrder(),
                field.getTitle(),
                field.getType(),
                field.isRequired(),
                field.getFileFormats(),
                field.getMaxFileSizeMb(),
                field.getParticipantHint(),
                field.getExampleValue(),
                field.getExpertNote(),
                criteria(field)
        );
    }

    public static FieldParticipantResponse toFieldParticipantResponse(SubmissionField field) {
        return new FieldParticipantResponse(
                field.getId(),
                field.getOrder(),
                field.getTitle(),
                field.getType(),
                field.isRequired(),
                field.getFileFormats(),
                field.getMaxFileSizeMb(),
                field.getParticipantHint(),
                field.getExampleValue()
        );
    }

    private static List<FieldCriterionResponse> criteria(SubmissionField field) {
        return field.getCriteria().stream()
                .sorted(Comparator.comparing(FieldCriterion::getOrder))
                .map(criterion -> new FieldCriterionResponse(
                        criterion.getId(),
                        criterion.getOrder(),
                        criterion.getDescription()
                ))
                .toList();
    }

}
