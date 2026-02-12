package com.cielo.adapter.persistence.pendingTask;

import enums.PendingTaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PendingTaskJPARepository extends JpaRepository<PendingTaskEntity, Long> {
    List<PendingTaskEntity> findByStatus(PendingTaskStatus status);
    boolean existsByOrderIdAndStatus(Long orderId, PendingTaskStatus status);
}
