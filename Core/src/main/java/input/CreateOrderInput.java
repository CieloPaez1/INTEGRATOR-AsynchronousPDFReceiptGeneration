package input;

import enums.OrderStatus;

import java.math.BigDecimal;

public interface CreateOrderInput {
    void createOrder(Long userId, BigDecimal amount);
    boolean stateChange(Long orderId, OrderStatus statusChangeRequest);
}
