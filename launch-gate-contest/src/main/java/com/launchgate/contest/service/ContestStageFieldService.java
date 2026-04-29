package com.launchgate.contest.service;

import com.launchgate.common.DomainException;
import com.launchgate.common.NotFoundException;
import com.launchgate.contest.dto.FieldParticipantResponse;
import com.launchgate.contest.dto.FieldResponse;
import com.launchgate.contest.dto.SubmissionFieldRequest;
import com.launchgate.contest.entity.ContestStatus;
import com.launchgate.contest.entity.ContestRole;
import com.launchgate.contest.entity.SubmissionField;
import com.launchgate.contest.repository.SubmissionFieldRepository;
import com.launchgate.contest.utils.field.SubmissionFieldMapper;
import com.launchgate.identity.dto.AuthenticatedUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ContestStageFieldService {
    private final SubmissionFieldRepository fieldRepository;
    private final ContestReaderService contestReaderService;
    private final SortOrderService sortOrderService;
    private final ContestRolePolicy rolePolicy;

    @Transactional(readOnly = true)
    public List<FieldResponse> organizerFields(Long stageId) {
        return fieldRepository.findAllByStageIdOrderByOrderAsc(stageId).stream()
                .map(SubmissionFieldMapper::toFieldResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<FieldParticipantResponse> participantFields(Long stageId) {
        var stage = contestReaderService.getStageById(stageId);
        if (stage.getContest().getStatus() == ContestStatus.DRAFT) {
            throw new NotFoundException("Stage not found");
        }
        return fieldRepository.findAllByStageIdOrderByOrderAsc(stageId).stream()
                .map(SubmissionFieldMapper::toFieldParticipantResponse)
                .toList();
    }

    @Transactional
    public FieldResponse create(AuthenticatedUser user, Long stageId, SubmissionFieldRequest request) {
        var stage = contestReaderService.getStageById(stageId);
        rolePolicy.requireAny(stage.getContestId(), user.id(), ContestRole.CREATOR, ContestRole.ADMIN);
        var fields = fieldRepository.findAllByStageIdOrderByOrderAsc(stageId);
        var targetOrder = sortOrderService.normalizeRequestedOrder(request.order(), fields.size());
        sortOrderService.moveOrder(fields, targetOrder);
        var field = new SubmissionField(stage, targetOrder, request.title(), request.type(), request.required());
        field.update(
                targetOrder,
                request.title(),
                request.type(),
                request.required(),
                request.fileFormats(),
                request.maxFileSizeMb(),
                request.options(),
                request.participantHint(),
                request.exampleValue(),
                request.expertNote(),
                request.criteriaDescription()
        );
        return SubmissionFieldMapper.toFieldResponse(fieldRepository.save(field));
    }

    @Transactional
    public FieldResponse update(AuthenticatedUser user, Long stageId, Long fieldId, SubmissionFieldRequest request) {
        var stage = contestReaderService.getStageById(stageId);
        rolePolicy.requireAny(stage.getContestId(), user.id(), ContestRole.CREATOR, ContestRole.ADMIN);
        var field = fieldRepository.findById(fieldId)
                .orElseThrow(() -> new DomainException("field_missing", "Submission field not found"));
        if (!field.getStageId().equals(stageId)) {
            throw new DomainException("field_missing", "Submission field not found");
        }
        sortOrderService.replaceOrder(field, fieldRepository.findAllByStageIdOrderByOrderAsc(stageId), request.order());
        field.update(
                field.getOrder(),
                request.title(),
                request.type(),
                request.required(),
                request.fileFormats(),
                request.maxFileSizeMb(),
                request.options(),
                request.participantHint(),
                request.exampleValue(),
                request.expertNote(),
                request.criteriaDescription()
        );
        return SubmissionFieldMapper.toFieldResponse(field);
    }

    @Transactional
    public Long delete(AuthenticatedUser user, Long stageId, Long fieldId) {
        var stage = contestReaderService.getStageById(stageId);
        rolePolicy.requireAny(stage.getContestId(), user.id(), ContestRole.CREATOR, ContestRole.ADMIN);
        var field = fieldRepository.findById(fieldId)
                .orElseThrow(() -> new DomainException("field_missing", "Submission field not found"));
        if (!field.getStageId().equals(stageId)) {
            throw new DomainException("field_missing", "Submission field not found");
        }
        fieldRepository.delete(field);
        sortOrderService.reorder(fieldRepository.findAllByStageIdOrderByOrderAsc(stageId));
        return fieldId;
    }
}
