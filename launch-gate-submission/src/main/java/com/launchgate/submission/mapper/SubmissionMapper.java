package com.launchgate.submission.mapper;

import com.launchgate.contest.dto.stage.StageParticipantResponse;
import com.launchgate.contest.entity.team.Team;
import com.launchgate.identity.entity.UserAccount;
import com.launchgate.submission.dto.ProjectResponse;
import com.launchgate.submission.dto.StageSubmissionResponse;
import com.launchgate.submission.dto.ValueResponse;
import com.launchgate.submission.entity.Project;
import com.launchgate.submission.entity.StageSubmission;
import com.launchgate.submission.entity.SubmissionValue;
import java.util.List;
import java.util.Optional;

import lombok.experimental.UtilityClass;

@UtilityClass
public class SubmissionMapper {

    public ProjectResponse toProjectResponse(Project project, List<StageSubmissionResponse> submissions) {
        Long teamId = Optional.ofNullable(project.getTeam())
                .map(Team::getId)
                .orElse(null);

        Long ownerParticipantId = Optional.ofNullable(project.getOwnerParticipant())
                .map(UserAccount::getId)
                .orElse(null);

        return new ProjectResponse(
                project.getId(),
                project.getContest().getId(),
                teamId,
                ownerParticipantId,
                submissions
        );
    }

    public StageSubmissionResponse toSubmissionResponse(StageSubmission submission, StageParticipantResponse stage, List<SubmissionValue> values) {
        return new StageSubmissionResponse(
                submission.getId(),
                submission.getStatus(),
                stage,
                values.stream()
                        .map(SubmissionMapper::toValueResponse)
                        .toList()
        );
    }

    private ValueResponse toValueResponse(SubmissionValue value) {
        return new ValueResponse(value.getId(), value.getField().getId(), value.getValueText(), value.getFileIds());
    }
}
