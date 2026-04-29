package com.launchgate.contest.controller;

import com.launchgate.contest.dto.DeletedResponse;
import com.launchgate.contest.dto.FieldParticipantListResponse;
import com.launchgate.contest.dto.FieldListResponse;
import com.launchgate.contest.dto.FieldResponse;
import com.launchgate.contest.dto.SubmissionFieldRequest;
import com.launchgate.contest.service.ContestStageFieldService;
import com.launchgate.identity.dto.AuthenticatedUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "Stage Fields", description = "Submission field management for contest stages")
public class ContestStageFieldController {
    private final ContestStageFieldService contestStageFieldService;

    @GetMapping("/organizer/stages/{stageId}/fields")
    @Operation(summary = "List stage submission fields for organizers")
    public FieldListResponse organizerFields(
            @PathVariable Long stageId
    ) {
        return new FieldListResponse(contestStageFieldService.organizerFields(stageId));
    }

    @GetMapping("/contests/stages/{stageId}/fields")
    @Operation(summary = "List stage submission fields for participants")
    public FieldParticipantListResponse participantFields(
            @PathVariable Long stageId
    ) {
        return new FieldParticipantListResponse(contestStageFieldService.participantFields(stageId));
    }

    @PostMapping("/organizer/stages/{stageId}/fields")
    @Operation(summary = "Create stage submission field")
    public FieldResponse create(
            @AuthenticationPrincipal AuthenticatedUser user,
            @PathVariable Long stageId,
            @Valid @RequestBody SubmissionFieldRequest request
    ) {
        return contestStageFieldService.create(user, stageId, request);
    }

    @PatchMapping("/organizer/stages/{stageId}/fields/{fieldId}")
    @Operation(summary = "Update stage submission field")
    public FieldResponse update(
            @AuthenticationPrincipal AuthenticatedUser user,
            @PathVariable Long stageId,
            @PathVariable Long fieldId,
            @Valid @RequestBody SubmissionFieldRequest request
    ) {
        return contestStageFieldService.update(user, stageId, fieldId, request);
    }

    @DeleteMapping("/organizer/stages/{stageId}/fields/{fieldId}")
    @Operation(summary = "Delete stage submission field")
    public DeletedResponse delete(
            @AuthenticationPrincipal AuthenticatedUser user,
            @PathVariable Long stageId,
            @PathVariable Long fieldId
    ) {
        return new DeletedResponse(contestStageFieldService.delete(user, stageId, fieldId));
    }
}
