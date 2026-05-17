package com.launchgate.contest.service.api;

import com.launchgate.contest.dto.stage.StageOrganizesResponse;
import com.launchgate.contest.dto.stage.StageParticipantResponse;
import com.launchgate.contest.dto.stage.StageRequest;
import com.launchgate.identity.dto.AuthenticatedUser;

import java.util.List;

/**
 * Сервис для управления этапами (стадиями) конкурса.
 * Обеспечивает создание, редактирование, удаление и получение информации об этапах
 * с автоматическим управлением порядком их следования и разграничением прав
 * доступа для организаторов и участников.
 */
public interface ContestStageService {

    /**
     * Создает новый этап в рамках указанного конкурса.
     * Автоматически пересчитывает и сдвигает порядковые номера существующих этапов.
     *
     * @param user      текущий аутентифицированный пользователь, создающий этап
     * @param contestId идентификатор конкурса, в который добавляется этап
     * @param request   данные запроса с параметрами этапа (дедлайн, система оценивания и т.д.)
     * @return ответ с полной информацией о созданном этапе для организатора
     */
    StageOrganizesResponse create(AuthenticatedUser user, Long contestId, StageRequest request);

    /**
     * Обновляет параметры существующего этапа конкурса.
     * При изменении порядкового номера автоматически корректирует позиции остальных этапов.
     *
     * @param user    текущий аутентифицированный пользователь, изменяющий этап
     * @param stageId идентификатор обновляемого этапа
     * @param request новые данные этапа, включая запрашиваемый порядковый номер
     * @return ответ с обновленной информацией об этапе для организатора
     */
    StageOrganizesResponse update(AuthenticatedUser user, Long stageId, StageRequest request);

    /**
     * Удаляет этап из конкурса.
     * После удаления автоматически переупорядочивает оставшиеся этапы, чтобы закрыть пропуски в нумерации.
     *
     * @param user    текущий аутентифицированный пользователь, удаляющий этап
     * @param stageId идентификатор удаляемого этапа
     * @return идентификатор удаленного этапа
     */
    Long delete(AuthenticatedUser user, Long stageId);

    /**
     * Возвращает список всех этапов конкурса со служебной информацией для организаторов.
     * Результаты отсортированы по порядковому номеру этапа.
     *
     * @param contestId идентификатор конкурса
     * @return список ответов с данными этапов для организатора
     */
    List<StageOrganizesResponse> organizerStages(Long contestId);

    /**
     * Возвращает детальную информацию о конкретном этапе для организатора.
     *
     * @param stageId идентификатор этапа
     * @return ответ с данными этапа для организатора
     */
    StageOrganizesResponse getOrganizerStage(Long stageId);

    /**
     * Возвращает список этапов конкурса, доступный для публичного просмотра участниками.
     * Результаты отсортированы по порядковому номеру.
     *
     * @param contestId идентификатор конкурса
     * @return список ответов с публичными данными этапов для участников
     */
    List<StageParticipantResponse> publicStages(Long contestId);

    /**
     * Возвращает публичную информацию о конкретном этапе для участников.
     *
     * @param stageId идентификатор этапа
     * @return ответ с публичными данными этапа для участника
     */
    StageParticipantResponse publicStage(Long stageId);
}
