package com.cielo.adapter.persistence.pendingTask;

import com.cielo.adapter.persistence.order.OrderEntity;
import enums.PendingTaskStatus;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "pending_tasks")
public class PendingTaskEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(optional = false)
    @JoinColumn(name = "order_id")
    private OrderEntity order;

    @Enumerated(EnumType.STRING)
    private PendingTaskStatus status;

    private LocalDateTime createdAt;
    private LocalDateTime processedAt;

    public PendingTaskEntity() {
    }
    public PendingTaskEntity(Long id, OrderEntity order, PendingTaskStatus status, LocalDateTime createdAt, LocalDateTime processedAt) {
        this.id = id;
        this.order = order;
        this.status = status;
        this.createdAt = createdAt;
        this.processedAt = processedAt;
    }
    public Long getId() { return id; }
    public OrderEntity getOrder() { return order; }
    public PendingTaskStatus getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getProcessedAt() { return processedAt; }

    public void setId(Long id) { this.id = id; }
    public void setOrder(OrderEntity order) { this.order = order; }
    public void setStatus(PendingTaskStatus status) { this.status = status; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setProcessedAt(LocalDateTime processedAt) { this.processedAt = processedAt; }

}
