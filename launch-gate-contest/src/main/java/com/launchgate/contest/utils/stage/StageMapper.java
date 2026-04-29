package com.launchgate.contest.utils.stage;

import com.launchgate.contest.dto.stage.StageOrganizesResponse;
import com.launchgate.contest.dto.stage.StageParticipantResponse;
import com.launchgate.contest.entity.ContestResource;
import com.launchgate.contest.entity.SubmissionField;
import com.launchgate.contest.entity.stage.ContestStage;
import com.launchgate.contest.utils.field.SubmissionFieldMapper;
import com.launchgate.contest.utils.resource.ResourceMapper;
import lombok.experimental.UtilityClass;

import java.util.Comparator;

@UtilityClass
public class StageMapper {

    public static StageOrganizesResponse toStageOrganizesResponse(
            ContestStage stage
    ) {
        var fields = stage.getFields().stream()
                .sorted(Comparator.comparing(SubmissionField::getOrder))
                .toList();
        var resources = stage.getResources().stream()
                .sorted(Comparator.comparing(ContestResource::getOrder))
                .toList();
        return new StageOrganizesResponse(
                stage.getId(),
                stage.getOrder(),
                stage.getTitle(),
                stage.getDescription(),
                stage.getRules(),
                stage.getExtraInfo(),
                stage.getDeadlineAt(),
                stage.isEliminating(),
                stage.getScoreScale(),
                fields.stream().map(SubmissionFieldMapper::toFieldResponse).toList(),
                resources.stream().map(ResourceMapper::toResourceResponse).toList()
        );
    }

    public static StageParticipantResponse toStageParticipantResponse(
            ContestStage stage
    ) {
        var fields = stage.getFields().stream()
                .sorted(Comparator.comparing(SubmissionField::getOrder))
                .toList();
        var resources = stage.getResources().stream()
                .sorted(Comparator.comparing(ContestResource::getOrder))
                .toList();
        return new StageParticipantResponse(
                stage.getId(),
                stage.getOrder(),
                stage.getTitle(),
                stage.getDescription(),
                stage.getRules(),
                stage.getDeadlineAt(),
                stage.isEliminating(),
                stage.getScoreScale(),
                fields.stream().map(SubmissionFieldMapper::toFieldParticipantResponse).toList(),
                resources.stream().map(ResourceMapper::toResourceResponse).toList()
        );
    }
}
