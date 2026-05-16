package com.launchgate.export.utils;

import lombok.experimental.UtilityClass;
import org.springframework.http.MediaType;

/**
 * Утилитный класс констант для работы с файлами.
 */
@UtilityClass
public class FileConstant {

    /**
     * Название файла выгрузки.
     */
    public String EXPORT_FILE_BASE_NAME = "contest-ranking";

    /**
     * Шаблон названия выгрузки.
     * [Название файла]-[Время создания файла].[Расширение файла]
     */
    public String EXPORT_FILE_NAME_TEMPLATE = "%s-%s.%s";

    /**
     * Расширение csv.
     */
    public String CSV_EXTENSION = "csv";

    /**
     * Расширение xlsx.
     */
    public String XLSX_EXTENSION = "xlsx";

    /**
     * MIME-тип для передачи документов в формате (.xlsx).
     */
    public MediaType MEDIA_TYPE_XLSX = MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

    /**
     * MIME-тип для передачи текстовых файлов в формате (.csv).
     */
    public MediaType MEDIA_TYPE_CSV = MediaType.valueOf("text/csv");
}
