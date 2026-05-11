package com.launchgate.contest.utils.field;

import com.launchgate.contest.dto.FieldFormatResponse;
import com.launchgate.contest.entity.SubmissionFieldFileFormat;
import lombok.experimental.UtilityClass;

@UtilityClass
public class SubmissionFieldFormatMapper {

    public static FieldFormatResponse toResponse(SubmissionFieldFileFormat format) {
        return new FieldFormatResponse(
                format.name(),
                format.extension(),
                format.category(),
                format.aiTextSupported()
        );
    }
}
