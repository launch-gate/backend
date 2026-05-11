package com.launchgate.contest.dto;

import com.launchgate.contest.entity.*;
import java.util.List;

public record FieldResponse(
        Long id,
        int order,
        String title,
        FieldType type,
        boolean required,
        List<SubmissionFieldFileFormat> fileFormats,
        Integer maxFileSizeMb,
        String participantHint,
        String exampleValue,
        String expertNote,
        List<FieldCriterionResponse> criteria
) {
}
