package com.launchgate.contest.service.impl;

import com.launchgate.common.LaunchGateException;
import com.launchgate.common.NotFoundException;
import com.launchgate.contest.dto.FieldCriterionRequest;
import com.launchgate.contest.dto.FieldFormatResponse;
import com.launchgate.contest.dto.FieldParticipantResponse;
import com.launchgate.contest.dto.FieldResponse;
import com.launchgate.contest.dto.SubmissionFieldRequest;
import com.launchgate.contest.enums.ContestStatus;
import com.launchgate.contest.enums.ContestRole;
import com.launchgate.contest.entity.FieldCriterion;
import com.launchgate.contest.entity.SubmissionField;
import com.launchgate.contest.entity.stage.ContestStage;
import com.launchgate.contest.repository.SubmissionFieldRepository;
import com.launchgate.contest.service.ContestReaderService;
import com.launchgate.contest.service.ContestRolePolicy;
import com.launchgate.contest.service.SubmissionFieldConfigurationService;
import com.launchgate.contest.service.api.ContestStageFieldService;
import com.launchgate.contest.utils.SortOrderService;
import com.launchgate.contest.utils.field.SubmissionFieldFormatMapper;
import com.launchgate.contest.utils.field.SubmissionFieldMapper;
import com.launchgate.identity.dto.AuthenticatedUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Реализация {@link ContestStageFieldService}.
 */
@Service
@RequiredArgsConstructor
public class ContestStageFieldServiceImpl implements ContestStageFieldService {
    private final SubmissionFieldRepository fieldRepository;
    private final ContestReaderService contestReaderService;
    private final ContestRolePolicy rolePolicy;
    private final SubmissionFieldConfigurationService submissionFieldConfigurationService;

    @Override
    @Transactional(readOnly = true)
    public List<FieldResponse> organizerFields(Long stageId) {
        return fieldRepository.findAllByStageIdOrderByOrderAsc(stageId).stream()
                .map(SubmissionFieldMapper::toFieldResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<FieldFormatResponse> supportedFormats() {
        return submissionFieldConfigurationService.allFormats().stream()
                .map(SubmissionFieldFormatMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<FieldParticipantResponse> participantFields(Long stageId) {
        ContestStage stage = contestReaderService.getStageById(stageId);
        if (stage.getContest().getStatus() == ContestStatus.DRAFT) {
            throw new NotFoundException("Стадия не найдена");
        }

        return fieldRepository.findAllByStageIdOrderByOrderAsc(stageId).stream()
                .map(SubmissionFieldMapper::toFieldParticipantResponse)
                .toList();
    }

    @Override
    @Transactional
    public FieldResponse create(AuthenticatedUser user, Long stageId, SubmissionFieldRequest request) {
        ContestStage stage = validateAccessAndGetStage(user, stageId);
        submissionFieldConfigurationService.validate(request);
        List<SubmissionField> fields = fieldRepository.findAllByStageIdOrderByOrderAsc(stageId);
        Integer targetOrder = SortOrderService.normalizeRequestedOrder(request.order(), fields.size());
        SortOrderService.moveOrder(fields, targetOrder);

        SubmissionField field = new SubmissionField(stage, targetOrder, request.title(), request.type(), request.required());

        field.update(
                targetOrder,
                request.title(),
                request.type(),
                request.required(),
                request.fileFormats(),
                request.maxFileSizeMb(),
                request.participantHint(),
                request.exampleValue(),
                request.expertNote()
        );
        field.replaceCriteria(buildCriteria(field, request.criteria()));
        return SubmissionFieldMapper.toFieldResponse(fieldRepository.save(field));
    }

    @Override
    @Transactional
    public FieldResponse update(AuthenticatedUser user, Long stageId, Long fieldId, SubmissionFieldRequest request) {
        validateAccessAndGetStage(user, stageId);
        submissionFieldConfigurationService.validate(request);

        SubmissionField field = fieldRepository.findById(fieldId)
                .orElseThrow(() -> new LaunchGateException("Поле формы не найдено"));

        if (!field.getStageId().equals(stageId)) {
            throw new LaunchGateException("Поле формы не найдено");
        }

        SortOrderService.replaceOrder(field, fieldRepository.findAllByStageIdOrderByOrderAsc(stageId), request.order());

        field.update(
                field.getOrder(),
                request.title(),
                request.type(),
                request.required(),
                request.fileFormats(),
                request.maxFileSizeMb(),
                request.participantHint(),
                request.exampleValue(),
                request.expertNote()
        );

        field.replaceCriteria(buildCriteria(field, request.criteria()));
        return SubmissionFieldMapper.toFieldResponse(field);
    }

    @Override
    @Transactional
    public Long delete(AuthenticatedUser user, Long stageId, Long fieldId) {
        validateAccessAndGetStage(user, stageId);

        SubmissionField field = fieldRepository.findById(fieldId)
                .orElseThrow(() -> new LaunchGateException("Поле формы не найдено"));

        if (!field.getStageId().equals(stageId)) {
            throw new LaunchGateException("Поле формы не найдено");
        }
        fieldRepository.delete(field);
        SortOrderService.reorder(fieldRepository.findAllByStageIdOrderByOrderAsc(stageId));
        return fieldId;
    }

    private ContestStage validateAccessAndGetStage(AuthenticatedUser user, Long stageId) {
        ContestStage stage = contestReaderService.getStageById(stageId);
        rolePolicy.requireAny(stage.getContestId(), user.id(), ContestRole.CREATOR, ContestRole.ADMIN);
        return stage;
    }

    private List<FieldCriterion> buildCriteria(SubmissionField field, List<FieldCriterionRequest> criteriaRequests) {
        if (CollectionUtils.isEmpty(criteriaRequests)) {
            return List.of();
        }

        List<FieldCriterion> criteria = new ArrayList<>();

        criteriaRequests.forEach(request -> {
            Integer targetOrder = SortOrderService.normalizeRequestedOrder(request.order(), criteria.size());
            SortOrderService.moveOrder(criteria, targetOrder);
            criteria.add(new FieldCriterion(field, targetOrder, request.description()));
        });

        SortOrderService.reorder(criteria);
        return criteria;
    }
}
