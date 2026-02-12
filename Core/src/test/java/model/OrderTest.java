package model;

import enums.OrderStatus;
import exception.OrderException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.mockito.Mockito.mock;

public class OrderTest {

    private User activeUser(LocalDateTime now) {
        User user = User.factory("john@example.com", "secret123", now);
        user.activate(now.plusMinutes(1));
        return user;
    }
    private Order createPendingOrder(LocalDateTime now) {
        return Order.factory(activeUser(now), BigDecimal.valueOf(100), now);
    }

    private Order createProcessingOrder(LocalDateTime now) {
        Order order = createPendingOrder(now);
        order.process(now.plusMinutes(1));
        return order;
    }
    @Test
    void createOrderInPendingState() {
        LocalDateTime now = LocalDateTime.now();

        Order order = createPendingOrder(now);

        Assertions.assertNotNull(order);
        Assertions.assertEquals(OrderStatus.PENDING, order.getStatus());
        Assertions.assertEquals(BigDecimal.valueOf(100), order.getAmount());
        Assertions.assertEquals(now, order.getCreatedAt());
        Assertions.assertEquals(now, order.getUpdatedAt());
    }

    @Test
    void throwExceptionWhenUserIsNotActive() {
        LocalDateTime now = LocalDateTime.now();
        User user = User.factory("john@example.com", "secret123", now);

        Assertions.assertThrows(OrderException.class, () ->
                Order.factory(user, BigDecimal.TEN, now)
        );
    }

    @Test
    void throwExceptionWhenAmountExceedsMaximum() {
        LocalDateTime now = LocalDateTime.now();
        User user = activeUser(now);

        Assertions.assertThrows(OrderException.class, () ->
                Order.factory(user, new BigDecimal("10000.01"), now)
        );
    }

    @Test
    void changeStateFromPendingToProcessing() {
        LocalDateTime now = LocalDateTime.now();
        Order order = createPendingOrder(now);

        LocalDateTime processTime = now.plusMinutes(1);
        order.process(processTime);

        Assertions.assertEquals(OrderStatus.PROCESSING, order.getStatus());
        Assertions.assertEquals(processTime, order.getUpdatedAt());
    }

    @Test
    void throwExceptionWhenProcessReceivesNullDate() {
        LocalDateTime now = LocalDateTime.now();
        Order order = createPendingOrder(now);

        Assertions.assertThrows(OrderException.class, () ->
                order.process(null)
        );
    }

    @Test
    void approveOrderWhenInProcessingState() {
        LocalDateTime now = LocalDateTime.now();
        Order order = createProcessingOrder(now);

        LocalDateTime approveTime = now.plusMinutes(2);
        order.approve(approveTime);

        Assertions.assertEquals(OrderStatus.APPROVED, order.getStatus());
        Assertions.assertTrue(order.isFinal());
        Assertions.assertEquals(approveTime, order.getUpdatedAt());
    }

    @Test
    void rejectOrderWhenInProcessingState() {
        LocalDateTime now = LocalDateTime.now();
        Order order = createProcessingOrder(now);

        LocalDateTime rejectTime = now.plusMinutes(2);
        order.reject(rejectTime);

        Assertions.assertEquals(OrderStatus.REJECTED, order.getStatus());
        Assertions.assertTrue(order.isFinal());
        Assertions.assertEquals(rejectTime, order.getUpdatedAt());
    }

    @Test
    void cancelOrderWhenInPendingState() {
        LocalDateTime now = LocalDateTime.now();
        Order order = createPendingOrder(now);

        LocalDateTime cancelTime = now.plusMinutes(1);
        order.cancel(cancelTime);

        Assertions.assertEquals(OrderStatus.CANCELLED, order.getStatus());
        Assertions.assertTrue(order.isFinal());
        Assertions.assertEquals(cancelTime, order.getUpdatedAt());
    }

    @Test
    void throwExceptionWhenApprovingFromInvalidState() {
        LocalDateTime now = LocalDateTime.now();
        Order order = createPendingOrder(now);

        Assertions.assertThrows(OrderException.class, () ->
                order.approve(now.plusMinutes(1))
        );
    }

    @Test
    void throwExceptionWhenCancellingFromInvalidState() {
        LocalDateTime now = LocalDateTime.now();
        Order order = createProcessingOrder(now);

        Assertions.assertThrows(OrderException.class, () ->
                order.cancel(now.plusMinutes(2))
        );
    }

    @Test
    void isFinalReturnsTrueOnlyForFinalStates() {
        LocalDateTime now = LocalDateTime.now();
        Order order = createPendingOrder(now);

        Assertions.assertFalse(order.isFinal());

        order.process(now.plusMinutes(1));
        Assertions.assertFalse(order.isFinal());

        order.approve(now.plusMinutes(2));
        Assertions.assertTrue(order.isFinal());
    }

    @Test
    void throwExceptionWhenFactoryReceivesInvalidData() {
        LocalDateTime now = LocalDateTime.now();
        User user = activeUser(now);

        Assertions.assertThrows(OrderException.class, () ->
                Order.factory(null, BigDecimal.TEN, now)
        );

        Assertions.assertThrows(OrderException.class, () ->
                Order.factory(user, null, now)
        );

        Assertions.assertThrows(OrderException.class, () ->
                Order.factory(user, BigDecimal.ZERO, now)
        );

        Assertions.assertThrows(OrderException.class, () ->
                Order.factory(user, BigDecimal.TEN, null)
        );
    }

    @Test
    void restoreRecreatesOrderWithGivenValues() {
        LocalDateTime now = LocalDateTime.now();
        User user = activeUser(now);

        Order restored = Order.restore(
                99L,
                user,
                OrderStatus.PROCESSING,
                BigDecimal.valueOf(100),
                now,
                now.plusMinutes(5)
        );

        Assertions.assertEquals(99L, restored.getId());
        Assertions.assertEquals(user, restored.getUser());
        Assertions.assertEquals(OrderStatus.PROCESSING, restored.getStatus());
        Assertions.assertEquals(BigDecimal.valueOf(100), restored.getAmount());
        Assertions.assertEquals(now, restored.getCreatedAt());
        Assertions.assertEquals(now.plusMinutes(5), restored.getUpdatedAt());
    }
}