package com.launchgate.contest.enums;

import java.util.Locale;
import java.util.Set;

public enum SubmissionFieldFileFormat {
    DOC("doc", FieldFileFormatCategory.DOCUMENT, true, Set.of("application/msword")),
    DOCX("docx", FieldFileFormatCategory.DOCUMENT, true, Set.of("application/vnd.openxmlformats-officedocument.wordprocessingml.document")),
    PDF("pdf", FieldFileFormatCategory.DOCUMENT, true, Set.of("application/pdf")),
    TXT("txt", FieldFileFormatCategory.DOCUMENT, true, Set.of("text/plain")),
    PPTX("pptx", FieldFileFormatCategory.DOCUMENT, false, Set.of("application/vnd.openxmlformats-officedocument.presentationml.presentation")),
    CSV("csv", FieldFileFormatCategory.DOCUMENT, false, Set.of("text/csv", "application/csv", "application/vnd.ms-excel")),
    XLSX("xlsx", FieldFileFormatCategory.DOCUMENT, false, Set.of("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")),
    JPG("jpg", FieldFileFormatCategory.IMAGE, false, Set.of("image/jpeg")),
    JPEG("jpeg", FieldFileFormatCategory.IMAGE, false, Set.of("image/jpeg")),
    PNG("png", FieldFileFormatCategory.IMAGE, false, Set.of("image/png")),
    WEBP("webp", FieldFileFormatCategory.IMAGE, false, Set.of("image/webp")),
    MP4("mp4", FieldFileFormatCategory.VIDEO, false, Set.of("video/mp4")),
    MOV("mov", FieldFileFormatCategory.VIDEO, false, Set.of("video/quicktime")),
    WEBM("webm", FieldFileFormatCategory.VIDEO, false, Set.of("video/webm"));

    private final String extension;
    private final FieldFileFormatCategory category;
    private final boolean aiTextSupported;
    private final Set<String> contentTypes;

    SubmissionFieldFileFormat(
            String extension,
            FieldFileFormatCategory category,
            boolean aiTextSupported,
            Set<String> contentTypes
    ) {
        this.extension = extension;
        this.category = category;
        this.aiTextSupported = aiTextSupported;
        this.contentTypes = contentTypes;
    }

    public String extension() {
        return extension;
    }

    public FieldFileFormatCategory category() {
        return category;
    }

    public boolean aiTextSupported() {
        return aiTextSupported;
    }

    public boolean matches(String originalFilename, String contentType) {
        return matchesExtension(originalFilename) || matchesContentType(contentType);
    }

    public boolean matchesExtension(String originalFilename) {
        return extension.equals(extractExtension(originalFilename));
    }

    public boolean matchesContentType(String contentType) {
        return contentType != null && contentTypes.contains(contentType.toLowerCase(Locale.ROOT));
    }

    public static String extractExtension(String originalFilename) {
        if (originalFilename == null || !originalFilename.contains(".")) {
            return "";
        }
        return originalFilename.substring(originalFilename.lastIndexOf('.') + 1).toLowerCase(Locale.ROOT);
    }
}
