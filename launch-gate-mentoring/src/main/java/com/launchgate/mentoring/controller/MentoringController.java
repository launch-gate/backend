package com.launchgate.mentoring.controller;

import com.launchgate.identity.dto.AuthenticatedUser;
import com.launchgate.mentoring.dto.AssignMentorRequest;
import com.launchgate.mentoring.dto.MentorAssignmentListResponse;
import com.launchgate.mentoring.dto.MentorAssignmentResponse;
import com.launchgate.mentoring.dto.MentorCallCreatedResponse;
import com.launchgate.mentoring.dto.MentorCallListResponse;
import com.launchgate.mentoring.dto.MentorCommentCreatedResponse;
import com.launchgate.mentoring.dto.MentorCommentListResponse;
import com.launchgate.mentoring.dto.MentorCommentRequest;
import com.launchgate.mentoring.dto.MentorCommentResponse;
import com.launchgate.mentoring.dto.ScheduleCallRequest;
import com.launchgate.mentoring.service.MentoringService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "Mentoring", description = "Mentor assignments, comments and calls")
@RequiredArgsConstructor
public class MentoringController {
    private final MentoringService mentoringService;

    @PostMapping("/organizer/mentors/assignments")
    @Operation(summary = "Assign mentor to a team")
    public MentorAssignmentResponse assign(
            @AuthenticationPrincipal AuthenticatedUser user,
            @Valid @RequestBody AssignMentorRequest request
    ) {
        return mentoringService.assign(user, request);
    }

    @GetMapping("/mentor/teams")
    @Operation(summary = "List teams assigned to current mentor")
    public MentorAssignmentListResponse myTeams(@AuthenticationPrincipal AuthenticatedUser user) {
        return new MentorAssignmentListResponse(mentoringService.myTeams(user));
    }

    @GetMapping("/mentor/calls")
    @Operation(summary = "List scheduled calls of current mentor")
    public MentorCallListResponse myCalls(@AuthenticationPrincipal AuthenticatedUser user) {
        return new MentorCallListResponse(mentoringService.myCalls(user));
    }

    @GetMapping("/teams/{teamId}/mentor-calls")
    @Operation(summary = "List mentor calls visible for team members and assigned mentor")
    public MentorCallListResponse teamCalls(
            @AuthenticationPrincipal AuthenticatedUser user,
            @PathVariable Long teamId
    ) {
        return new MentorCallListResponse(mentoringService.teamCalls(user, teamId));
    }

    @GetMapping("/mentor/stage-submissions/{stageSubmissionId}")
    @Operation(summary = "Open team stage submission visible to mentor and team members")
    public com.launchgate.submission.dto.StageSubmissionResponse stageSubmission(
            @AuthenticationPrincipal AuthenticatedUser user,
            @PathVariable Long stageSubmissionId
    ) {
        return mentoringService.stageSubmission(user, stageSubmissionId);
    }

    @GetMapping("/stage-submissions/{stageSubmissionId}/mentor-comments")
    @Operation(summary = "List mentor comments for a stage submission")
    public MentorCommentListResponse comments(
            @AuthenticationPrincipal AuthenticatedUser user,
            @PathVariable Long stageSubmissionId
    ) {
        return new MentorCommentListResponse(mentoringService.comments(user, stageSubmissionId));
    }

    @PostMapping("/mentor/comments")
    @Operation(summary = "Create mentor comment for a stage submission")
    public MentorCommentCreatedResponse comment(
            @AuthenticationPrincipal AuthenticatedUser user,
            @Valid @RequestBody MentorCommentRequest request
    ) {
        return new MentorCommentCreatedResponse(mentoringService.comment(user, request));
    }

    @PostMapping("/mentor/calls")
    @Operation(summary = "Schedule mentor call")
    public MentorCallCreatedResponse scheduleCall(
            @AuthenticationPrincipal AuthenticatedUser user,
            @Valid @RequestBody ScheduleCallRequest request
    ) {
        return new MentorCallCreatedResponse(mentoringService.scheduleCall(user, request));
    }
}
