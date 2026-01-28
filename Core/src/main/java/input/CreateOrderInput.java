package input;

import java.math.BigDecimal;

public interface CreateOrderInput {
    void createOrder(Long userId, BigDecimal amount);

}
