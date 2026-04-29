package com.launchgate.export.controller;

import lombok.RequiredArgsConstructor;

import com.launchgate.export.dto.*;
import com.launchgate.export.entity.*;
import com.launchgate.export.service.*;
import com.launchgate.identity.dto.AuthenticatedUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/organizer/contests/{contestId}")
@RequiredArgsConstructor
@Tag(name = "Exports", description = "Contest analytics and export API")
public class ExportController {
    private final ExportService exportService;

    @GetMapping("/analytics")
    @Operation(summary = "Get contest analytics snapshot")
    public ContestAnalyticsResponse analytics(@AuthenticationPrincipal AuthenticatedUser user, @PathVariable Long contestId) {
        return exportService.analytics(user, contestId);
    }

    @GetMapping("/exports/ranking")
    @Operation(summary = "Download contest ranking as CSV or XLSX")
    public ResponseEntity<byte[]> ranking(
            @AuthenticationPrincipal AuthenticatedUser user,
            @PathVariable Long contestId,
            @RequestParam(defaultValue = "CSV") ExportFormat format
    ) {
        var body = exportService.ranking(user, contestId, format);
        var extension = format == ExportFormat.CSV ? "csv" : "xlsx";
        var mediaType = format == ExportFormat.CSV
                ? new MediaType("text", "csv")
                : MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        return ResponseEntity.ok()
                .contentType(mediaType)
                .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment()
                        .filename("contest-ranking." + extension)
                        .build()
                        .toString())
                .body(body);
    }

    @PostMapping("/exports/custom")
    @Operation(summary = "Create custom export job placeholder")
    public CustomExportResponse custom(
            @AuthenticationPrincipal AuthenticatedUser user,
            @PathVariable Long contestId,
            @Valid @RequestBody CustomExportRequest request
    ) {
        return exportService.createCustomExport(user, contestId, request);
    }
}
