package com.launchgate.aievaluation.service;

import com.launchgate.common.ForbiddenException;
import com.launchgate.contest.entity.ContestRole;
import com.launchgate.contest.entity.stage.ContestStage;
import com.launchgate.contest.service.ContestRolePolicy;
import com.launchgate.identity.dto.AuthenticatedUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AiEvaluationAccessService {
    private final ContestRolePolicy contestRolePolicy;

    public void requireReviewAccess(AuthenticatedUser user, ContestStage stage) {
        if (contestRolePolicy.hasAny(stage.getContestId(), user.id(), ContestRole.CREATOR, ContestRole.ADMIN, ContestRole.EXPERT)) {
            return;
        }
        throw new ForbiddenException("AI review is available only to organizers and experts of this contest");
    }
}
