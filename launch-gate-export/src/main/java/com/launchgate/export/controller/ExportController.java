package com.launchgate.export.controller;

import com.launchgate.export.dto.ContestAnalyticsResponse;
import com.launchgate.export.dto.CustomExportRequest;
import com.launchgate.export.dto.CustomExportResponse;
import com.launchgate.export.entity.ExportFormat;
import com.launchgate.export.service.AnalyticsService;
import com.launchgate.export.service.ExportService;
import com.launchgate.export.utils.ExportUtils;
import lombok.RequiredArgsConstructor;

import com.launchgate.identity.dto.AuthenticatedUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Контроллер для получения статистики и выгрузок по конкурсу.
 */
@RestController
@RequestMapping("/api/v1/organizer/contests/{contestId}")
@RequiredArgsConstructor
@Tag(name = "Выгрузка работ", description = "API для получения статистики и выгрузок по конкурсам")
public class ExportController {

    private final ExportService exportService;
    private final AnalyticsService analyticsService;

    @GetMapping("/analytics")
    @Operation(summary = "Получить аналитику по конкурсу")
    public ContestAnalyticsResponse analytics(@AuthenticationPrincipal AuthenticatedUser user,
                                              @PathVariable("contestId") Long contestId) {
        return analyticsService.analytics(user, contestId);
    }

    @GetMapping("/exports/ranking")
    @Operation(summary = "Выгрузить турнирную таблицу в формате CSV или XLSX")
    public ResponseEntity<byte[]> ranking(@AuthenticationPrincipal AuthenticatedUser user, @PathVariable("contestId") Long contestId,
                                          @RequestParam(defaultValue = "CSV") ExportFormat format) {
        byte[] body = exportService.ranking(user, contestId, format);
        return ExportUtils.createExportResponse(body, format);
    }

    @PostMapping("/exports/custom")
    @Operation(summary = "Получить настраиваемую выгрузку")
    public CustomExportResponse custom(@AuthenticationPrincipal AuthenticatedUser user, @PathVariable("contestId") Long contestId,
                                       @Valid @RequestBody CustomExportRequest request) {
        return exportService.createCustomExport(user, contestId, request);
    }
}
