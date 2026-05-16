package com.launchgate.export.service.impl;

import com.launchgate.evaluation.dto.ReviewSummary;
import com.launchgate.evaluation.service.EvaluationReviewService;
import com.launchgate.export.mapper.ExportMapper;
import com.launchgate.export.service.ExportService;
import com.launchgate.export.utils.ExportUtils;
import com.launchgate.identity.service.UserService;
import com.launchgate.submission.service.SubmissionReaderService;
import lombok.RequiredArgsConstructor;

import com.launchgate.export.dto.*;
import com.launchgate.export.entity.*;
import com.launchgate.export.repository.*;

import com.launchgate.contest.service.ContestReaderService;
import com.launchgate.contest.entity.ContestRole;
import com.launchgate.contest.entity.stage.ContestStage;
import com.launchgate.contest.service.ContestRolePolicy;
import com.launchgate.identity.dto.AuthenticatedUser;
import com.launchgate.submission.dto.SubmissionSummary;
import java.time.Clock;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Реализация сервиса для формирования отчетов по конкурсу.
 */
@Service
@RequiredArgsConstructor
public class ExportServiceImpl implements ExportService {
    private final ContestReaderService contestReaderService;
    private final ContestRolePolicy rolePolicy;
    private final SubmissionReaderService submissionReaderService;
    private final EvaluationReviewService evaluationReviewService;
    private final ExportJobRepository exportJobRepository;
    private final UserService userService;
    private final Clock clock;

    @Override
    @Transactional(readOnly = true)
    public byte[] ranking(AuthenticatedUser user, Long contestId, ExportFormat format) {
        rolePolicy.requireAny(contestId, user.id(), ContestRole.CREATOR, ContestRole.ADMIN);

        List<RankingRow> rows = rankingRows(contestId);
        return switch (format) {
            case CSV -> ExportUtils.toCsv(rows);
            case XLSX -> ExportUtils.toXlsx(rows);
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
        ReviewSummary review = evaluationReviewService.summary(submission.submissionId());
        return ExportMapper.toRankingRow(stage, submission, review);
    }
}
