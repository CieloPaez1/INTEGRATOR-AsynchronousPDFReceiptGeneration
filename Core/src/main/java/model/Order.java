package model;

import enums.OrderStatus;
import exception.OrderException;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Order {
    private Long id;
    private final User user;
    private OrderStatus status;
    private final BigDecimal amount;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private Order(Long id, User user, OrderStatus status, BigDecimal amount,
                  LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.user = user;
        this.status = status;
        this.amount = amount;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }


    public static Order factory(User user, BigDecimal amount, LocalDateTime now) {
        if (user == null) {
            throw new OrderException("User cannot be null");
        }
        if (!user.isActive()) {
            throw new OrderException("Only ACTIVE users may create orders");
        }
        if (amount == null || amount.signum() <= 0) {
            throw new OrderException("Order amount must be greater than zero");
        }
        if (now == null) {
            throw new NullPointerException("Current time cannot be null");
        }

        return new Order(
                null,
                user,
                OrderStatus.PENDING,
                amount,
                now,
                now
        );
    }


    public void process(LocalDateTime now) {
        ensureStatus(OrderStatus.PENDING);
        update(now);
        status = OrderStatus.PROCESSING;
    }

    public void approve(LocalDateTime now) {
        ensureStatus(OrderStatus.PROCESSING);
        update(now);
        status = OrderStatus.APPROVED;
    }

    public void reject(LocalDateTime now) {
        ensureStatus(OrderStatus.PROCESSING);
        update(now);
        status = OrderStatus.REJECTED;
    }

    public void cancel(LocalDateTime now) {
        ensureStatus(OrderStatus.PENDING);
        update(now);
        status = OrderStatus.CANCELLED;
    }



    private void ensureStatus(OrderStatus expected) {
        if (status != expected) {
            throw new OrderException(
                    "Invalid order state. Expected " + expected + " but was " + status
            );
        }
    }

    private void update(LocalDateTime now) {
        if (now == null) {
            throw new OrderException("Current time cannot be null");
        }
        updatedAt = now;
    }

    public boolean isFinal() {
        return status == OrderStatus.APPROVED
                || status == OrderStatus.REJECTED
                || status == OrderStatus.CANCELLED;
    }
}

