package com.launchgate.export.utils;

import com.launchgate.common.LaunchGateException;
import com.launchgate.export.dto.RankingRow;
import com.launchgate.export.entity.ExportFormat;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;

/**
 * Утилитный класс для обработки выгрузок.
 */
@Slf4j
@UtilityClass
public class ExportUtils {

    /**
     * Сформировать ответ настраиваемой выгрузки.
     *
     * @param body   выгрузка
     * @param format формат экспорта
     * @return выгрузка
     */
    public static ResponseEntity<byte[]> createExportResponse(byte[] body, ExportFormat format) {
        String extension = switch (format) {
            case CSV -> FileConstant.CSV_EXTENSION;
            case XLSX -> FileConstant.XLSX_EXTENSION;
        };

        MediaType mediaType = switch (format) {
            case CSV -> FileConstant.MEDIA_TYPE_CSV;
            case XLSX -> FileConstant.MEDIA_TYPE_XLSX;
        };

        return ResponseEntity.ok()
                .contentType(mediaType)
                .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment()
                        .filename(generateFileName(extension))
                        .build()
                        .toString())
                .body(body);
    }

    /**
     * Создать название файла на основе текущего времени.
     *
     * @param extension расширение файла
     * @return название файла
     */
    public static String generateFileName(String extension) {
        return String.format(FileConstant.EXPORT_FILE_NAME_TEMPLATE, FileConstant.EXPORT_FILE_BASE_NAME, LocalDate.now(), extension);
    }

    /**
     * Преобразовать выгрузку в файл csv формата.
     *
     * @param rows ранговая выгрузка
     * @return csv файл
     */
    public static byte[] toCsv(List<RankingRow> rows) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (CSVPrinter printer = new CSVPrinter(new OutputStreamWriter(out, StandardCharsets.UTF_8), CSVFormat.DEFAULT)) {
            printer.printRecord("stage", "project", "submission_id", "average_score", "completed_reviews");

            for (RankingRow row : rows) {
                printer.printRecord(
                        row.stage(),
                        row.project(),
                        row.submissionId(),
                        row.score(),
                        row.completedReviews()
                );
            }

            printer.flush();
        } catch (Exception e) {
            log.error("Ошибка при генерации CSV файла", e);
            throw new LaunchGateException("Ошибка при генерации CSV файла");
        }

        return out.toByteArray();
    }

    /**
     * Преобразовать выгрузку в файл xlsx формата.
     *
     * @param rows ранговая выгрузка
     * @return xlsx файл
     */
    public static byte[] toXlsx(List<RankingRow> rows) {
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Ranking");
            Row header = sheet.createRow(0);

            header.createCell(0).setCellValue("Stage");
            header.createCell(1).setCellValue("Project");
            header.createCell(2).setCellValue("Submission ID");
            header.createCell(3).setCellValue("Average score");
            header.createCell(4).setCellValue("Completed reviews");

            int index = 1;
            for (RankingRow rankingRow : rows) {
                Row row = sheet.createRow(index++);

                row.createCell(0).setCellValue(rankingRow.stage());
                row.createCell(1).setCellValue(rankingRow.project());
                row.createCell(2).setCellValue(rankingRow.submissionId().toString());
                row.createCell(3).setCellValue(rankingRow.score().doubleValue());
                row.createCell(4).setCellValue(rankingRow.completedReviews());
            }

            workbook.write(out);
            return out.toByteArray();
        } catch (Exception exception) {
            log.error("Ошибка во время создания xlsx файла", exception);
            throw new LaunchGateException("Ошибка во время создания xlsx файла");
        }
    }
}
