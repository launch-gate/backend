package com.launchgate.aievaluation.service.extractor;

import com.launchgate.common.DomainException;
import com.launchgate.filestorage.service.StoredFileContent;
import java.io.ByteArrayInputStream;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.springframework.stereotype.Component;

@Component
public class DocFileTextExtractorStrategy implements FileTextExtractorStrategy {
    @Override
    public boolean supports(StoredFileContent file) {
        return "doc".equals(FileTextExtractorSupport.extension(file));
    }

    @Override
    public String extract(StoredFileContent file) {
        try (var inputStream = new ByteArrayInputStream(file.bytes());
             var document = new HWPFDocument(inputStream);
             var extractor = new WordExtractor(document)) {
            return extractor.getText();
        } catch (Exception exception) {
            throw new DomainException("doc_extract_failed", "Could not extract text from doc file");
        }
    }
}
