package com.launchgate.contest.utils;

import com.launchgate.common.LaunchGateException;
import com.launchgate.contest.entity.order.BaseOrderEntity;
import lombok.experimental.UtilityClass;

import java.util.List;

@UtilityClass
public class SortOrderService {

    public static Integer normalizeRequestedOrder(Integer requestedOrder, int currentSize) {
        if (requestedOrder == null) {
            return currentSize;
        }
        if (requestedOrder < 0 || requestedOrder > currentSize) {
            throw new LaunchGateException("Порядок сортировки выходит за допустимый диапазон");
        }
        return requestedOrder;
    }

    public static <T extends BaseOrderEntity> void moveOrder(List<T> entities, Integer targetOrder) {
        if (targetOrder == null || targetOrder >= entities.size()) {
            return;
        }
        entities.subList(targetOrder, entities.size())
                .forEach(BaseOrderEntity::increaseOrder);
    }

    public static <T extends BaseOrderEntity> void replaceOrder(T updatedEntity, List<T> entities, Integer requestedOrder) {
        if (requestedOrder == null) {
            return;
        }
        entities.removeIf(entity -> entity.getId().equals(updatedEntity.getId()));
        var targetOrder = normalizeRequestedOrder(requestedOrder, entities.size());
        entities.add(targetOrder, updatedEntity);
        reorder(entities);
    }

    public static <T extends BaseOrderEntity> void reorder(List<T> entities) {
        for (int i = 0; i < entities.size(); i ++) {
            entities.get(i).setOrder(i);
        }
    }
}
