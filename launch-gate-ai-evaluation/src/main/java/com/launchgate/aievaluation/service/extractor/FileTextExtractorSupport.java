package com.launchgate.aievaluation.service.extractor;

import com.launchgate.filestorage.service.StoredFileContent;
import java.util.Locale;

public final class FileTextExtractorSupport {

    private FileTextExtractorSupport() {
    }

    public static String extension(StoredFileContent file) {
        var filename = file.originalFilename();
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf('.') + 1).toLowerCase(Locale.ROOT);
    }
}
