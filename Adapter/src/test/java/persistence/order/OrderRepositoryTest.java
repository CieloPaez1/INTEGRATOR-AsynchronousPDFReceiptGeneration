package persistence.order;

import com.cielo.adapter.persistence.order.OrderEntity;
import com.cielo.adapter.persistence.order.OrderJPARepository;
import com.cielo.adapter.persistence.order.OrderRepository;
import com.cielo.adapter.persistence.user.UserEntity;
import com.cielo.adapter.persistence.user.UserJPARepository;
import exception.OrderException;
import model.Order;
import model.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import output.OrderOutput;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class OrderRepositoryTest {

    @Mock
    private OrderJPARepository orderJPARepository;

    @Mock
    private UserJPARepository userJPARepository;

    @InjectMocks
    private OrderRepository orderRepository;

    private User activeUser() {
        LocalDateTime now = LocalDateTime.now();
        User user = User.factory("john@example.com", "secret123", now);
        user.setId(1L);
        user.activate(now.plusMinutes(1));
        return user;
    }

    @Test
    public void saveOrderUserNotFoundTest() {

        User user = activeUser();
        Order order = Order.factory(user, BigDecimal.valueOf(100), LocalDateTime.now());

        when(userJPARepository.findById(1L)).thenReturn(Optional.empty());

        Assertions.assertThrows(OrderException.class, () ->
                orderRepository.saveOrder(order)
        );
    }

    @Test
    public void saveOrderFailsWhenIdNullTest() {

        User user = activeUser();
        Order order = Order.factory(user, BigDecimal.valueOf(100), LocalDateTime.now());

        UserEntity userEntity = new UserEntity();
        userEntity.setId(1L);

        OrderEntity savedEntity = new OrderEntity();

        when(userJPARepository.findById(1L)).thenReturn(Optional.of(userEntity));
        when(orderJPARepository.save(any(OrderEntity.class))).thenReturn(savedEntity);

        boolean result = orderRepository.saveOrder(order);

        Assertions.assertFalse(result);
    }

    @Test
    public void findUserByIdReturnsUserTest() {

        UserEntity entity = new UserEntity();
        entity.setId(1L);

        when(userJPARepository.findById(1L)).thenReturn(Optional.of(entity));

        User result = orderRepository.findUserById(1L);

        Assertions.assertNotNull(result);
    }

    @Test
    public void findUserByIdReturnsNullTest() {

        when(userJPARepository.findById(1L)).thenReturn(Optional.empty());

        User result = orderRepository.findUserById(1L);

        Assertions.assertNull(result);
    }

    @Test
    public void findByIdReturnsOrderTest() {

        OrderEntity entity = new OrderEntity();
        entity.setId(5L);

        when(orderJPARepository.findById(5L)).thenReturn(Optional.of(entity));

        Order result = orderRepository.findById(5L);

        Assertions.assertNotNull(result);
    }

    @Test
    public void findByIdReturnsNullTest() {

        when(orderJPARepository.findById(5L)).thenReturn(Optional.empty());

        Order result = orderRepository.findById(5L);

        Assertions.assertNull(result);
    }
}
