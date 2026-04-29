package com.launchgate.submission.service;

import com.launchgate.contest.dto.stage.StageParticipantResponse;
import com.launchgate.submission.dto.ProjectResponse;
import com.launchgate.submission.dto.StageSubmissionResponse;
import com.launchgate.submission.dto.ValueResponse;
import com.launchgate.submission.entity.Project;
import com.launchgate.submission.entity.StageSubmission;
import com.launchgate.submission.entity.SubmissionValue;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SubmissionMapper {

    public static ProjectResponse toProjectResponse(Project project, List<StageSubmissionResponse> submissions) {
        return new ProjectResponse(
                project.getId(),
                project.getContestId(),
                project.getTeamId(),
                project.getOwnerParticipantId(),
                submissions
        );
    }

    public static StageSubmissionResponse toSubmissionResponse(
            StageSubmission submission,
            StageParticipantResponse stage,
            List<SubmissionValue> values
    ) {
        return new StageSubmissionResponse(
                submission.getId(),
                submission.getStatus(),
                stage,
                values.stream().map(SubmissionMapper::toValueResponse).toList()
        );
    }

    private static ValueResponse toValueResponse(SubmissionValue value) {
        return new ValueResponse(value.getId(), value.getFieldId(), value.getValueText(), value.getFileIds());
    }
}
