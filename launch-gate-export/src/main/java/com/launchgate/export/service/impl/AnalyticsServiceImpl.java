package com.launchgate.export.service.impl;

import com.launchgate.contest.dto.ContestMetrics;
import com.launchgate.contest.entity.ContestRole;
import com.launchgate.contest.service.ContestReaderService;
import com.launchgate.contest.service.ContestRolePolicy;
import com.launchgate.export.dto.ContestAnalyticsResponse;
import com.launchgate.export.service.AnalyticsService;
import com.launchgate.identity.dto.AuthenticatedUser;
import com.launchgate.submission.service.SubmissionReaderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Реализация сервиса для формирования аналитики по конкурсу.
 */
@Service
@RequiredArgsConstructor
public class AnalyticsServiceImpl implements AnalyticsService {

    private final ContestRolePolicy rolePolicy;
    private final ContestReaderService contestReaderService;
    private final SubmissionReaderService submissionReaderService;

    @Override
    @Transactional(readOnly = true)
    public ContestAnalyticsResponse analytics(AuthenticatedUser user, Long contestId) {

        rolePolicy.requireAny(contestId, user.id(), ContestRole.CREATOR, ContestRole.ADMIN);

        ContestMetrics metrics = contestReaderService.metrics(contestId);

        Long submittedWorks = submissionReaderService.countAllSubmittedStage(contestId);

        return new ContestAnalyticsResponse(metrics.registrations(), metrics.teams(), metrics.stages(), submittedWorks);
    }
}
