package com.launchgate.aievaluation.service.extractor;

import com.launchgate.common.DomainException;
import com.launchgate.filestorage.dto.StoredFileContent;
import java.io.ByteArrayInputStream;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.stereotype.Component;

@Component
public class DocxFileTextExtractorStrategy implements FileTextExtractorStrategy {
    @Override
    public boolean supports(StoredFileContent file) {
        return "docx".equals(FileTextExtractorSupport.extension(file));
    }

    @Override
    public String extract(StoredFileContent file) {
        try (var inputStream = new ByteArrayInputStream(file.bytes());
             var document = new XWPFDocument(inputStream);
             var extractor = new XWPFWordExtractor(document)) {
            return extractor.getText();
        } catch (Exception exception) {
            throw new DomainException("docx_extract_failed", "Could not extract text from docx file");
        }
    }
}
