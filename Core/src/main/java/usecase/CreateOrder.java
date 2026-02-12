package usecase;

import enums.OrderStatus;
import exception.OrderException;
import input.CreateOrderInput;
import model.Order;
import model.User;
import output.OrderOutput;


import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDateTime;

public class CreateOrder implements CreateOrderInput {
    private final OrderOutput orderOutput;
    private final Clock clock;

    public CreateOrder(OrderOutput orderOutput, Clock clock) {
        this.orderOutput = orderOutput;
        this.clock = clock;
    }

    @Override
    public void createOrder(Long userId, BigDecimal amount) {

        User user = orderOutput.findUserById(userId);

        if (user == null) {
            throw new OrderException("User not found");
        }
        LocalDateTime now = LocalDateTime.now(clock);

        Order order = Order.factory(user, amount, now);

        if (!orderOutput.saveOrder(order)) {
            throw new OrderException("Failed to save order");
        }

    }

    @Override
    public boolean stateChange(Long orderId, OrderStatus statusChangeRequest) {
        if (orderId == null) {
            throw new OrderException("Order not found");
        }

        Order order = orderOutput.findById(orderId);

        if (order == null) {
            throw new OrderException("Order not found");
        }

        if (statusChangeRequest == null) {
            throw new OrderException("Status change request cannot be null");
        }

        LocalDateTime now = LocalDateTime.now(clock);

        switch (statusChangeRequest) {
            case PROCESSING -> order.process(now);
            case APPROVED -> order.approve(now);
            case REJECTED -> order.reject(now);
            case CANCELLED -> order.cancel(now);
            default -> throw new OrderException("Invalid status transition");
        }

        return orderOutput.saveOrder(order);

    }


}
