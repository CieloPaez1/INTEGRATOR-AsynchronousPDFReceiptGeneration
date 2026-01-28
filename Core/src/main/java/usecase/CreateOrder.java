package usecase;

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
}
