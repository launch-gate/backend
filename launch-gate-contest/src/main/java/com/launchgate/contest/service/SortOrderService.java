package com.launchgate.contest.service;

import com.launchgate.common.DomainException;
import com.launchgate.contest.entity.order.BaseOrderEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SortOrderService {

    public int normalizeRequestedOrder(Integer requestedOrder, int currentSize) {
        if (requestedOrder == null) {
            return currentSize;
        }
        if (requestedOrder < 0 || requestedOrder > currentSize) {
            throw new DomainException("bad_order", "Sort order is out of range");
        }
        return requestedOrder;
    }

    public <T extends BaseOrderEntity> void moveOrder(
            List<T> entities,
            Integer targetOrder
    ) {
        if (targetOrder == null || targetOrder >= entities.size()) {
            return;
        }
        entities.subList(targetOrder, entities.size())
                .forEach(BaseOrderEntity::increaseOrder);
    }

    public <T extends BaseOrderEntity> void replaceOrder(
            T updatedEntity,
            List<T> entities,
            Integer requestedOrder
    ) {
        if (requestedOrder == null) {
            return;
        }
        entities.removeIf(entity -> entity.getId().equals(updatedEntity.getId()));
        var targetOrder = normalizeRequestedOrder(requestedOrder, entities.size());
        entities.add(targetOrder, updatedEntity);
        reorder(entities);
    }

    public <T extends BaseOrderEntity> void reorder(
            List<T> entities
    ) {
        for (int i = 0; i < entities.size(); i ++) {
            entities.get(i).setOrder(i);
        }
    }

}
