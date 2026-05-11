package com.launchgate.contest.dto;

import com.launchgate.contest.entity.FieldFileFormatCategory;

public record FieldFormatResponse(
        String code,
        String extension,
        FieldFileFormatCategory category,
        boolean aiTextSupported
) {
}
