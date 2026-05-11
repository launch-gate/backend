package com.launchgate.aievaluation.service.extractor;

import com.launchgate.filestorage.service.StoredFileContent;

public interface FileTextExtractorStrategy {
    boolean supports(StoredFileContent file);

    String extract(StoredFileContent file);
}
