package com.launchgate.contest.utils.field;

import com.launchgate.contest.dto.FieldParticipantResponse;
import com.launchgate.contest.dto.FieldResponse;
import com.launchgate.contest.entity.SubmissionField;
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
                field.getOptions(),
                field.getParticipantHint(),
                field.getExampleValue(),
                field.getExpertNote(),
                field.getCriteriaDescription()
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
                field.getOptions(),
                field.getParticipantHint(),
                field.getExampleValue()
        );
    }

}
