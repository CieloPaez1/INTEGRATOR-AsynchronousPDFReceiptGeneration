package model;

import enums.PendingTaskStatus;
import exception.PendingTaskException;

import java.time.LocalDateTime;

public class PendingTask {
    private Long id;
    private final Order order;
    private PendingTaskStatus status;
    private final LocalDateTime createdAt;
    private LocalDateTime processedAt;

    private PendingTask(
            Long id,
            Order order,
            PendingTaskStatus status,
            LocalDateTime createdAt,
            LocalDateTime processedAt
    ) {
        this.id = id;
        this.order = order;
        this.status = status;
        this.createdAt = createdAt;
        this.processedAt = processedAt;
    }
    public static PendingTask factory(Order order, LocalDateTime now) {
        if (order == null) {
            throw new PendingTaskException("Order cannot be null");
        }
        if (now == null) {
            throw new PendingTaskException("Current time cannot be null");
        }

        return new PendingTask(
                null,
                order,
                PendingTaskStatus.PENDING,
                now,
                null
        );
    }



    public void markDone(LocalDateTime now) {
        if (now == null) {
            throw new PendingTaskException("Current time cannot be null");
        }
        this.status = PendingTaskStatus.DONE;
        this.processedAt = now;
    }

    public void markError(LocalDateTime now) {
        if (now == null) {
            throw new PendingTaskException("Current time cannot be null");
        }
        this.status = PendingTaskStatus.ERROR;
        this.processedAt = now;
    }

    public boolean isPending() {
        return status == PendingTaskStatus.PENDING;
    }

    public PendingTaskStatus getStatus() {return status;}
    public Long getId() {return id;}
    public Order getOrder() {return order;}
    public LocalDateTime getCreatedAt() {return createdAt;}
    public LocalDateTime getProcessedAt() {
        return processedAt;
    }


    public void setId(Long id) {
        this.id = id;
    }
}
