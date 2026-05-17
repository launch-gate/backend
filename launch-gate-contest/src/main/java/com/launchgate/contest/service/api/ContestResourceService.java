package com.launchgate.contest.service.api;

import com.launchgate.contest.dto.resources.ResourceRequest;
import com.launchgate.contest.dto.resources.ResourceResponse;
import com.launchgate.identity.dto.AuthenticatedUser;

import java.util.List;

/**
 * Сервис для управления ресурсами (материалами, файлами, ссылками) этапов конкурса.
 * Обеспечивает создание, обновление, удаление и получение ресурсов с автоматическим
 * управлением порядком их отображения и проверкой прав доступа.
 */
public interface ContestResourceService {

    /**
     * Создает новый ресурс для указанного этапа конкурса.
     * Автоматически пересчитывает и сдвигает порядковые номера существующих ресурсов.
     *
     * @param user      текущий аутентифицированный пользователь, создающий ресурс
     * @param stageId   идентификатор этапа конкурса, к которому добавляется ресурс
     * @param request   данные запроса, содержащие параметры нового ресурса (тип, название, ссылку и т.д.)
     * @return ответ с данными созданного ресурса и его финальным порядковым номером
     */
    ResourceResponse createStageResource(AuthenticatedUser user, Long stageId, ResourceRequest request);

    /**
     * Обновляет параметры существующего ресурса этапа конкурса.
     * При изменении порядкового номера ресурса автоматически корректирует позиции остальных элементов.
     *
     * @param user       текущий аутентифицированный пользователь, обновляющий ресурс
     * @param stageId    идентификатор этапа конкурса
     * @param resourceId идентификатор обновляемого ресурса
     * @param request    новые данные ресурса, включая запрашиваемый порядковый номер
     * @return ответ с обновленными данными ресурса
     */
    ResourceResponse updateStageResource(AuthenticatedUser user, Long stageId, Long resourceId, ResourceRequest request);

    /**
     * Удаляет ресурс из этапа конкурса.
     * После удаления автоматически переупорядочивает оставшиеся ресурсы, чтобы закрыть пропуски в нумерации.
     *
     * @param user       текущий аутентифицированный пользователь, удаляющий ресурс
     * @param stageId    идентификатор этапа конкурса
     * @param resourceId идентификатор удаляемого ресурса
     * @return идентификатор удаленного ресурса
     */
    Long deleteStageResource(AuthenticatedUser user, Long stageId, Long resourceId);

    /**
     * Возвращает список всех ресурсов, привязанных к конкретному этапу конкурса.
     * Доступ к методу открытый (публичный просмотр), проверка прав ролей не требуется.
     *
     * @param stageId идентификатор этапа конкурса
     * @return список ответов с данными ресурсов, отсортированных по их порядку
     */
    List<ResourceResponse> getResourcesByStageId(Long stageId);
}

