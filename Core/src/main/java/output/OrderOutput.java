package output;

import model.Order;
import model.User;


public interface OrderOutput {
    boolean saveOrder(Order order);
    User findUserById(Long id);
    Order findById(Long orderId);
}
