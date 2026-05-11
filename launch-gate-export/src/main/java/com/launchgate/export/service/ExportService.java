package com.launchgate.export.service;

import com.launchgate.export.dto.CustomExportRequest;
import com.launchgate.export.dto.CustomExportResponse;
import com.launchgate.export.entity.ExportFormat;
import com.launchgate.identity.dto.AuthenticatedUser;

/**
 * Сервис для формирования отчетов по конкурсу
 */
public interface ExportService {

    /**
     * Сформировать турнирную таблицу по конкурсу.
     * @param user пользователь
     * @param contestId идентификатор конкурса
     * @param format формат выгрузки
     * @return выгрузка
     */
    byte[] ranking(AuthenticatedUser user, Long contestId, ExportFormat format);

    /**
     * Сформировать настраиваемую выгрузку по конкурсу
     * @param user пользователь
     * @param contestId идентификатор конкурса
     * @param request параметры выгрузки
     * @return выгрузка
     */
    CustomExportResponse createCustomExport(AuthenticatedUser user, Long contestId, CustomExportRequest request);
}
