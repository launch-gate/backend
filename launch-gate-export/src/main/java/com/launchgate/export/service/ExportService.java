package com.launchgate.export.service;

import lombok.RequiredArgsConstructor;

import com.launchgate.export.dto.*;
import com.launchgate.export.entity.*;
import com.launchgate.export.repository.*;

import com.launchgate.contest.service.ContestReaderService;
import com.launchgate.contest.entity.ContestRole;
import com.launchgate.contest.entity.stage.ContestStage;
import com.launchgate.contest.service.ContestRolePolicy;
import com.launchgate.evaluation.service.EvaluationCatalog;
import com.launchgate.identity.dto.AuthenticatedUser;
import com.launchgate.identity.service.UserService;
import com.launchgate.submission.dto.SubmissionSummary;
import com.launchgate.submission.service.SubmissionReaderService;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ExportService {
    private final ContestReaderService contestReaderService;
    private final ContestRolePolicy rolePolicy;
    private final SubmissionReaderService submissionReaderService;
    private final EvaluationCatalog evaluationCatalog;
    private final ExportJobRepository exportJobRepository;
    private final UserService userService;
    private final Clock clock;
    @Transactional(readOnly = true)
    public ContestAnalyticsResponse analytics(AuthenticatedUser user, Long contestId) {
        rolePolicy.requireAny(contestId, user.id(), ContestRole.CREATOR, ContestRole.ADMIN);
        var metrics = contestReaderService.metrics(contestId);
        var submittedWorks = contestReaderService.stages(contestId).stream()
                .mapToLong(stage -> submissionReaderService.getSubmittedSubmissionCountByStage(stage.getId()))
                .sum();
        return new ContestAnalyticsResponse(metrics.registrations(), metrics.teams(), metrics.stages(), submittedWorks);
    }

    @Transactional(readOnly = true)
    public byte[] ranking(AuthenticatedUser user, Long contestId, ExportFormat format) {
        rolePolicy.requireAny(contestId, user.id(), ContestRole.CREATOR, ContestRole.ADMIN);
        var rows = rankingRows(contestId);
        return switch (format) {
            case CSV -> toCsv(rows);
            case XLSX -> toXlsx(rows);
        };
    }

    @Transactional
    public CustomExportResponse createCustomExport(AuthenticatedUser user, Long contestId, CustomExportRequest request) {
        rolePolicy.requireAny(contestId, user.id(), ContestRole.CREATOR, ContestRole.ADMIN);
        var contest = contestReaderService.getContestById(contestId);
        var creator = userService.getUserById(user.id());
        var job = exportJobRepository.save(new ExportJob(contest, creator, request.format(), request.prompt(), Instant.now(clock)));
        var preview = "Custom export job accepted. AI adapter is not connected yet; default ranking columns will be used as fallback.";
        return new CustomExportResponse(job.getId(), preview);
    }

    private List<RankingRow> rankingRows(Long contestId) {
        return contestReaderService.stages(contestId).stream()
                .flatMap(stage -> submissionReaderService.getSubmittedSubmissionsByStage(stage.getId()).stream()
                        .map(submission -> rankingRow(stage, submission)))
                .sorted(Comparator.comparing(RankingRow::score).reversed())
                .toList();
    }

    private RankingRow rankingRow(ContestStage stage, SubmissionSummary submission) {
        var review = evaluationCatalog.summary(submission.submissionId());
        return ExportMapper.toRankingRow(stage, submission, review);
    }

    private byte[] toCsv(List<RankingRow> rows) {
        var builder = new StringBuilder("stage,project,submission_id,average_score,completed_reviews\n");
        for (var row : rows) {
            builder.append(escape(row.stage())).append(',')
                    .append(escape(row.project())).append(',')
                    .append(row.submissionId()).append(',')
                    .append(row.score()).append(',')
                    .append(row.completedReviews()).append('\n');
        }
        return builder.toString().getBytes(StandardCharsets.UTF_8);
    }

    private byte[] toXlsx(List<RankingRow> rows) {
        try (var workbook = new XSSFWorkbook(); var out = new ByteArrayOutputStream()) {
            var sheet = workbook.createSheet("Ranking");
            var header = sheet.createRow(0);
            header.createCell(0).setCellValue("Stage");
            header.createCell(1).setCellValue("Project");
            header.createCell(2).setCellValue("Submission ID");
            header.createCell(3).setCellValue("Average score");
            header.createCell(4).setCellValue("Completed reviews");
            var index = 1;
            for (var rankingRow : rows) {
                var row = sheet.createRow(index++);
                row.createCell(0).setCellValue(rankingRow.stage());
                row.createCell(1).setCellValue(rankingRow.project());
                row.createCell(2).setCellValue(rankingRow.submissionId().toString());
                row.createCell(3).setCellValue(rankingRow.score().doubleValue());
                row.createCell(4).setCellValue(rankingRow.completedReviews());
            }
            workbook.write(out);
            return out.toByteArray();
        } catch (Exception exception) {
            throw new IllegalStateException("Could not create xlsx export", exception);
        }
    }

    private String escape(String value) {
        if (value == null) {
            return "";
        }
        return "\"" + value.replace("\"", "\"\"") + "\"";
    }
}
