package com.launchgate.submission.service;

import com.launchgate.identity.dto.AuthenticatedUser;
import com.launchgate.submission.dto.OrganizerStageSubmissionListResponse;
import com.launchgate.submission.dto.StageSubmissionResponse;
import com.launchgate.submission.dto.ValueRequest;

/**
 * Сервис управления отправками и черновиками форм этапов.
 * Отвечает за валидацию, сохранение промежуточных значений и финальную подачу заявок участниками.
 */
public interface SubmissionService {

    /**
     * Получить все отправленные решения этапа для пространства организатора.
     *
     * @param user    пользователь
     * @param stageId идентификатор стадии
     * @return список отправленных решений
     */
    OrganizerStageSubmissionListResponse organizerStageSubmissions(AuthenticatedUser user, Long stageId);

    /**
     * Сохранение значения в черновике формы этапа.
     *
     * @param user      пользователь
     * @param projectId идентификатор проекта
     * @param stageId   идентификатор стадии
     * @param request   значение формы
     * @return форма этапа
     */
    StageSubmissionResponse saveValue(AuthenticatedUser user, Long projectId, Long stageId, ValueRequest request);

    /**
     * Завершить отправку формы этапа.
     *
     * @param user      пользователь
     * @param projectId идентификатор проекта
     * @param stageId   идентификатор стадии
     * @return форма
     */
    StageSubmissionResponse submit(AuthenticatedUser user, Long projectId, Long stageId);

    /**
     * Получить подробную информацию по форме этапа для пространства организатора.
     *
     * @param user         пользователь
     * @param submissionId идентификатор решения этапа
     * @return форма этапа
     */
    StageSubmissionResponse organizerSubmission(AuthenticatedUser user, Long submissionId);
}
