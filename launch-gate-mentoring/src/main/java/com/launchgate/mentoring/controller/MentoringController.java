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
import com.launchgate.mentoring.dto.ScheduleCallRequest;
import com.launchgate.mentoring.service.MentoringServiceImpl;
import com.launchgate.submission.dto.StageSubmissionResponse;
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

/**
 * Контроллер для назначения менторов, и работе с командой.
 */
@RestController
@RequestMapping("/api/v1")
@Tag(name = "Менторство", description = "Назначения менторов, комментарии и встречи")
@RequiredArgsConstructor
public class MentoringController {
    private final MentoringServiceImpl mentoringService;

    @PostMapping("/organizer/mentors/assignments")
    @Operation(summary = "Назначить ментора к команде")
    public MentorAssignmentResponse assign(
            @AuthenticationPrincipal AuthenticatedUser user,
            @Valid @RequestBody AssignMentorRequest request) {
        return mentoringService.assign(user, request);
    }

    @GetMapping("/mentor/teams")
    @Operation(summary = "Получить список команд назначенных ментору")
    public MentorAssignmentListResponse myTeams(@AuthenticationPrincipal AuthenticatedUser user) {
        return new MentorAssignmentListResponse(mentoringService.myTeams(user));
    }

    @GetMapping("/mentor/calls")
    @Operation(summary = "Получить список запланированных созвонов текущего ментора")
    public MentorCallListResponse myCalls(@AuthenticationPrincipal AuthenticatedUser user) {
        return new MentorCallListResponse(mentoringService.myCalls(user));
    }

    @GetMapping("/teams/{teamId}/mentor-calls")
    @Operation(summary = "Получить список созвонов с ментором, видимых участникам команды и назначенному ментору")
    public MentorCallListResponse teamCalls(
            @AuthenticationPrincipal AuthenticatedUser user,
            @PathVariable Long teamId) {
        return new MentorCallListResponse(mentoringService.teamCalls(user, teamId));
    }

    @GetMapping("/mentor/stage-submissions/{stageSubmissionId}")
    @Operation(summary = "Просмотреть отправленную работу команды, доступную наставнику и членам команды")
    public StageSubmissionResponse stageSubmission(
            @AuthenticationPrincipal AuthenticatedUser user,
            @PathVariable Long stageSubmissionId) {
        return mentoringService.stageSubmission(user, stageSubmissionId);
    }

    @GetMapping("/stage-submissions/{stageSubmissionId}/mentor-comments")
    @Operation(summary = "Получить список комментариев ментора к отправленному решению этапа")
    public MentorCommentListResponse comments(
            @AuthenticationPrincipal AuthenticatedUser user,
            @PathVariable Long stageSubmissionId) {
        return new MentorCommentListResponse(mentoringService.comments(user, stageSubmissionId));
    }

    @PostMapping("/mentor/comments")
    @Operation(summary = "Создать комментарий ментора к отправленному решению этапа")
    public MentorCommentCreatedResponse comment(
            @AuthenticationPrincipal AuthenticatedUser user,
            @Valid @RequestBody MentorCommentRequest request) {
        return new MentorCommentCreatedResponse(mentoringService.comment(user, request));
    }

    @PostMapping("/mentor/calls")
    @Operation(summary = "Запланировать встречу с ментором")
    public MentorCallCreatedResponse scheduleCall(
            @AuthenticationPrincipal AuthenticatedUser user,
            @Valid @RequestBody ScheduleCallRequest request) {
        return new MentorCallCreatedResponse(mentoringService.scheduleCall(user, request));
    }
}
