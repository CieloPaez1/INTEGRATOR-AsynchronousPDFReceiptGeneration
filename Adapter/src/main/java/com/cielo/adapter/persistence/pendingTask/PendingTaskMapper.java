package com.cielo.adapter.persistence.pendingTask;

import com.cielo.adapter.persistence.order.OrderEntity;
import com.cielo.adapter.persistence.order.OrderMapper;
import model.PendingTask;

public class PendingTaskMapper {
    public static PendingTaskEntity coreToEntity(PendingTask task) {
        if (task == null) return null;

        OrderEntity orderEntity = OrderMapper.coreToEntity(task.getOrder());
        return new PendingTaskEntity(
                task.getId(),
                orderEntity,
                task.getStatus(),
                task.getCreatedAt(),
                task.getProcessedAt()
        );
    }

    public static PendingTask entityToCore(PendingTaskEntity entity) {
        if (entity == null) return null;

        return PendingTask.restore(
                entity.getId(),
                OrderMapper.entityToCore(entity.getOrder()),
                entity.getStatus(),
                entity.getCreatedAt(),
                entity.getProcessedAt()
        );
    }
}
