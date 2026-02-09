package com.cielo.adapter.persistence.pendingTask;

import enums.PendingTaskStatus;
import model.PendingTask;
import output.PendingTaskOutput;

import java.util.List;

public class PendingTaskRepository implements PendingTaskOutput {

    private final PendingTaskJPARepository jpa;

    public PendingTaskRepository(PendingTaskJPARepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public boolean save(PendingTask task) {
        PendingTaskEntity entity = PendingTaskMapper.coreToEntity(task);
        PendingTaskEntity saved = jpa.save(entity);
        return saved.getId() != null;
    }

    @Override
    public List<PendingTask> findAllPending() {
        return jpa.findByStatus(PendingTaskStatus.PENDING)
                .stream()
                .map(PendingTaskMapper::entityToCore)
                .toList();
    }

    @Override
    public void update(PendingTask task) {
        PendingTaskEntity entity = PendingTaskMapper.coreToEntity(task);
        jpa.save(entity);

    }

    @Override
    public boolean existsPendingForOrder(Long orderId) {
        return jpa.existsByOrderIdAndStatus(orderId, PendingTaskStatus.PENDING);
    }
}
