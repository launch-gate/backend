package com.launchgate.contest.utils.stage;

import com.launchgate.contest.dto.FieldParticipantResponse;
import com.launchgate.contest.dto.FieldResponse;
import com.launchgate.contest.dto.resources.ResourceResponse;
import com.launchgate.contest.dto.stage.StageOrganizesResponse;
import com.launchgate.contest.dto.stage.StageParticipantResponse;
import com.launchgate.contest.entity.ContestResource;
import com.launchgate.contest.entity.SubmissionField;
import com.launchgate.contest.entity.stage.ContestStage;
import com.launchgate.contest.utils.field.SubmissionFieldMapper;
import com.launchgate.contest.utils.resource.ResourceMapper;
import lombok.experimental.UtilityClass;

import java.util.Comparator;
import java.util.List;

@UtilityClass
public class StageMapper {

    public static StageOrganizesResponse toStageOrganizesResponse(ContestStage stage) {
        List<FieldResponse> fields = stage.getFields().stream()
                .sorted(Comparator.comparing(SubmissionField::getOrder))
                .map(SubmissionFieldMapper::toFieldResponse)
                .toList();

        List<ResourceResponse> resources = stage.getResources().stream()
                .sorted(Comparator.comparing(ContestResource::getOrder))
                .map(ResourceMapper::toResourceResponse)
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
                fields,
                resources
        );
    }

    public static StageParticipantResponse toStageParticipantResponse(ContestStage stage) {
        List<FieldParticipantResponse> fields = stage.getFields().stream()
                .sorted(Comparator.comparing(SubmissionField::getOrder))
                .map(SubmissionFieldMapper::toFieldParticipantResponse)
                .toList();

        List<ResourceResponse> resources = stage.getResources().stream()
                .sorted(Comparator.comparing(ContestResource::getOrder))
                .map(ResourceMapper::toResourceResponse)
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
                fields,
                resources
        );
    }
}
