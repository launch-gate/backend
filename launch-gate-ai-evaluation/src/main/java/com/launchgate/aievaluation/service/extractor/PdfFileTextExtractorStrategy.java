package com.launchgate.aievaluation.service.extractor;

import com.launchgate.common.DomainException;
import com.launchgate.filestorage.dto.StoredFileContent;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Component;

@Component
public class PdfFileTextExtractorStrategy implements FileTextExtractorStrategy {
    @Override
    public boolean supports(StoredFileContent file) {
        return "pdf".equals(FileTextExtractorSupport.extension(file))
                || "application/pdf".equalsIgnoreCase(file.contentType());
    }

    @Override
    public String extract(StoredFileContent file) {
        try (var document = Loader.loadPDF(file.bytes())) {
            return new PDFTextStripper().getText(document);
        } catch (Exception exception) {
            throw new DomainException("pdf_extract_failed", "Could not extract text from pdf file");
        }
    }
}
