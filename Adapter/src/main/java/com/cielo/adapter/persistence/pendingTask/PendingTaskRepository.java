package com.cielo.adapter.persistence.pendingTask;

import model.PendingTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import output.PendingTaskOutput;

import java.util.List;
import java.util.stream.Collectors;

@Repository
public class PendingTaskRepository implements PendingTaskOutput {

    private final PendingTaskJPARepository jpa;
    @Autowired
    public PendingTaskRepository(PendingTaskJPARepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public boolean save(PendingTask task) {
        if (task == null) return false;

        PendingTaskEntity saved = jpa.save(PendingTaskMapper.coreToEntity(task));
        if (saved.getId() != null) {
            task.setId(saved.getId());
            return true;
        }
        return false;
    }

    @Override
    public List<PendingTask> findAllPending() {
        return jpa.findByStatus(enums.PendingTaskStatus.PENDING)
                .stream()
                .map(PendingTaskMapper::entityToCore)
                .collect(Collectors.toList());
    }

    @Override
    public void update(PendingTask task) {
        if (task == null || task.getId() == null) return;

        jpa.save(PendingTaskMapper.coreToEntity(task));
    }

    @Override
    public boolean existsPendingForOrder(Long orderId) {
        if (orderId == null) return false;
        return jpa.existsByOrderIdAndStatus(orderId, enums.PendingTaskStatus.PENDING);
    }
}
