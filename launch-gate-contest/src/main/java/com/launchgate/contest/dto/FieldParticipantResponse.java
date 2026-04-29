package com.launchgate.contest.dto;

import com.launchgate.contest.entity.FieldType;

public record FieldParticipantResponse(
        Long id,
        int order,
        String title,
        FieldType type,
        boolean required,
        String fileFormats,
        Integer maxFileSizeMb,
        String options,
        String participantHint,
        String exampleValue
) {
}
