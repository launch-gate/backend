package com.launchgate.contest.dto;

import com.launchgate.contest.entity.FieldType;
import com.launchgate.contest.entity.SubmissionFieldFileFormat;
import java.util.List;

public record FieldParticipantResponse(
        Long id,
        int order,
        String title,
        FieldType type,
        boolean required,
        List<SubmissionFieldFileFormat> fileFormats,
        Integer maxFileSizeMb,
        String participantHint,
        String exampleValue
) {
}
