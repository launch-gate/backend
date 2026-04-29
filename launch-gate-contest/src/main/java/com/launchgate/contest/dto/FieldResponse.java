package com.launchgate.contest.dto;

import com.launchgate.contest.entity.*;

public record FieldResponse(
        Long id,
        int order,
        String title,
        FieldType type,
        boolean required,
        String fileFormats,
        Integer maxFileSizeMb,
        String options,
        String participantHint,
        String exampleValue,
        String expertNote,
        String criteriaDescription
) {
}
