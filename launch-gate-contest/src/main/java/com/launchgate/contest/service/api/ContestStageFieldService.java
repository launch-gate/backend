package com.launchgate.contest.service.api;

import com.launchgate.contest.dto.FieldFormatResponse;
import com.launchgate.contest.dto.FieldParticipantResponse;
import com.launchgate.contest.dto.FieldResponse;
import com.launchgate.contest.dto.SubmissionFieldRequest;
import com.launchgate.identity.dto.AuthenticatedUser;

import java.util.List;

/**
 * Сервис для управления кастомными полями формы подачи решений на этапах конкурса.
 * Обеспечивает создание, редактирование, удаление и получение конфигурации полей
 */
public interface ContestStageFieldService {

    /**
     * Возвращает полный список полей этапа конкурса для организатора, отсортированный по порядку.
     * Включает служебную информацию (например, заметки для экспертов).
     *
     * @param stageId идентификатор этапа конкурса
     * @return список ответов с полной конфигурацией полей формы
     */
    List<FieldResponse> organizerFields(Long stageId);

    /**
     * Возвращает список всех поддерживаемых системой типов полей и форматов файлов.
     * Используется на фронтенде для отрисовки конструктора форм.
     *
     * @return список ответов со справочником доступных форматов данных
     */
    List<FieldFormatResponse> supportedFormats();

    /**
     * Возвращает список полей этапа конкурса, адаптированный для участников.
     * Скрывает внутренние данные организаторов и запрещает просмотр полей, если конкурс является черновиком.
     *
     * @param stageId идентификатор этапа конкурса
     * @return список ответов с полями формы для отображения участнику
     */
    List<FieldParticipantResponse> participantFields(Long stageId);

    /**
     * Создает новое поле формы в рамках указанного этапа конкурса.
     * Выполняет валидацию параметров конфигурации и сдвигает порядок существующих полей при необходимости.
     *
     * @param user    текущий аутентифицированный пользователь, создающий поле
     * @param stageId идентификатор этапа конкурса, в который добавляется поле
     * @param request данные запроса с конфигурацией поля и списком критериев оценки
     * @return ответ с конфигурацией созданного поля
     */
    FieldResponse create(AuthenticatedUser user, Long stageId, SubmissionFieldRequest request);

    /**
     * Обновляет параметры и критерии существующего поля формы.
     * При изменении порядкового номера поля автоматически пересчитывает позиции остальных элементов.
     *
     * @param user    текущий аутентифицированный пользователь, изменяющий поле
     * @param stageId идентификатор этапа конкурса
     * @param fieldId идентификатор обновляемого поля формы
     * @param request новые данные конфигурации поля
     * @return ответ с обновленной конфигурацией поля
     */
    FieldResponse update(AuthenticatedUser user, Long stageId, Long fieldId, SubmissionFieldRequest request);

    /**
     * Удаляет поле формы из этапа конкурса.
     * После удаления автоматически переупорядочивает оставшиеся поля, чтобы убрать пропуски в нумерации.
     *
     * @param user    текущий аутентифицированный пользователь, удаляющий поле
     * @param stageId идентификатор этапа конкурса
     * @param fieldId идентификатор удаляемого поля формы
     * @return идентификатор удаленного поля
     */
    Long delete(AuthenticatedUser user, Long stageId, Long fieldId);
}

