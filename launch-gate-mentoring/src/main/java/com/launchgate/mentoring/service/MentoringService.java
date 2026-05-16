package com.launchgate.mentoring.service;

import com.launchgate.identity.dto.AuthenticatedUser;
import com.launchgate.mentoring.dto.AssignMentorRequest;
import com.launchgate.mentoring.dto.MentorAssignmentResponse;
import com.launchgate.mentoring.dto.MentorCallResponse;
import com.launchgate.mentoring.dto.MentorCommentRequest;
import com.launchgate.mentoring.dto.MentorCommentResponse;
import com.launchgate.mentoring.dto.ScheduleCallRequest;
import com.launchgate.submission.dto.StageSubmissionResponse;

import java.util.List;

/**
 * Сервис для взаимодействия с менторами.
 */
public interface MentoringService {

    /**
     * Назначить ментора.
     * @param organizer организатор.
     * @param request запрос на создание ментора.
     * @return результат на назначение ментора для команды.
     */
    MentorAssignmentResponse assign(AuthenticatedUser organizer, AssignMentorRequest request);

    /**
     * Получить список команд назначенных ментору.
     * @param mentor ментор.
     * @return список команд назначенных ментору.
     */
    List<MentorAssignmentResponse> myTeams(AuthenticatedUser mentor);

    /**
     * Получить список запланированных созвонов текущего ментора.
     * @param mentor ментор.
     * @return информации о встрече с ментром.
     */
    List<MentorCallResponse> myCalls(AuthenticatedUser mentor);

    /**
     * Получить список комментариев ментора к отправленному решению этапа.
     * @param user пользователь.
     * @param stageSubmissionId идентификатор решения.
     * @return данные комментария ментора.
     */
    List<MentorCommentResponse> comments(AuthenticatedUser user, Long stageSubmissionId);

    /**
     * Получить список созвонов с ментором, видимых участникам команды и назначенному ментору.
     * @param user пользователь.
     * @param teamId идентификатор команды.
     * @return информации о встрече с ментром.
     */
    List<MentorCallResponse> teamCalls(AuthenticatedUser user, Long teamId);

    /**
     * Просмотреть отправленную работу команды, доступную наставнику и членам команды.
     * @param user пользователь.
     * @param stageSubmissionId идентификатор решения.
     * @return работа.
     */
    StageSubmissionResponse stageSubmission(AuthenticatedUser user, Long stageSubmissionId);

    /**
     * Создать комментарий ментора к отправленному решению этапа.
     * @param mentor ментор.
     * @param request комментарий ментора.
     * @return идентификатор комментария.
     */
    Long comment(AuthenticatedUser mentor, MentorCommentRequest request);

    /**
     * Запланировать созвон с ментором.
     * @param mentor ментор.
     * @param request запрос на планирование встречи с ментором.
     * @return идентификатор встречи.
     */
    Long scheduleCall(AuthenticatedUser mentor, ScheduleCallRequest request);
}