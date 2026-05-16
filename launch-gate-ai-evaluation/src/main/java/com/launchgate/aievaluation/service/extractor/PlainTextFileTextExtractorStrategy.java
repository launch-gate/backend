package com.launchgate.aievaluation.service.extractor;

import com.launchgate.filestorage.dto.StoredFileContent;
import java.nio.charset.StandardCharsets;
import org.springframework.stereotype.Component;

@Component
public class PlainTextFileTextExtractorStrategy implements FileTextExtractorStrategy {
    @Override
    public boolean supports(StoredFileContent file) {
        return "txt".equals(FileTextExtractorSupport.extension(file))
                || "text/plain".equalsIgnoreCase(file.contentType());
    }

    @Override
    public String extract(StoredFileContent file) {
        return new String(file.bytes(), StandardCharsets.UTF_8);
    }
}
