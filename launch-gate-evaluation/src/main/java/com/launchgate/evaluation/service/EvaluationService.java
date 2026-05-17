package com.launchgate.evaluation.service;

import com.launchgate.evaluation.dto.AssignmentRequest;
import com.launchgate.evaluation.dto.AssignmentResponse;
import com.launchgate.evaluation.dto.ReviewDraftRequest;
import com.launchgate.evaluation.dto.ReviewResponse;
import com.launchgate.evaluation.entity.ReviewStatus;
import com.launchgate.identity.dto.AuthenticatedUser;
import com.launchgate.submission.dto.StageSubmissionResponse;

import java.util.List;

/**
 * Сервис формирования экспертной оценки.
 */
public interface EvaluationService {

    /**
     * Назначить эксперта.
     *
     * @param organizer организатор.
     * @param request   запрос на назначение эксперта.
     * @return результат назначения эксперта.
     */
    AssignmentResponse assign(AuthenticatedUser organizer, AssignmentRequest request);

    /**
     * Получить список назначенных проверок текущего эксперта.
     *
     * @param expert эксперт.
     * @param status стаус проверки.
     * @return список назначенных проверок.
     */
    List<AssignmentResponse> myAssignments(AuthenticatedUser expert, ReviewStatus status);

    /**
     * Сохранить черновик оценки и комментария по проверке.
     *
     * @param expert       эксперт.
     * @param assignmentId идентификатор назначения.
     * @param request      черновик проверки.
     * @return данные экспертной проверки.
     */
    ReviewResponse saveDraft(AuthenticatedUser expert, Long assignmentId, ReviewDraftRequest request);

    /**
     * Опубликовать проверку.
     *
     * @param expert       эксперт.
     * @param assignmentId идентификатор назначения.
     * @return результат экспертной проверки.
     */
    ReviewResponse publish(AuthenticatedUser expert, Long assignmentId);

    /**
     * Получить отправленное на этап решение.
     *
     * @param expert       эксперт.
     * @param assignmentId идентификатор назначения.
     * @return решение.
     */
    StageSubmissionResponse reviewSubmission(AuthenticatedUser expert, Long assignmentId);
}