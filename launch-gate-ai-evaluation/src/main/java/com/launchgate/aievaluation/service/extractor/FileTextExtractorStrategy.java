package com.launchgate.aievaluation.service.extractor;

import com.launchgate.filestorage.dto.StoredFileContent;

public interface FileTextExtractorStrategy {
    boolean supports(StoredFileContent file);

    String extract(StoredFileContent file);
}
