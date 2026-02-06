package model;

import enums.OrderStatus;
import exception.OrderException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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
    void changeStateFromPendingToProcessing() {
        LocalDateTime now = LocalDateTime.now();

        Order order = createPendingOrder(now);
        order.process(now.plusMinutes(1));

        Assertions.assertEquals(OrderStatus.PROCESSING, order.getStatus());
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
        order.approve(now.plusMinutes(1));

        Assertions.assertEquals(OrderStatus.APPROVED, order.getStatus());
        Assertions.assertTrue(order.isFinal());
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
    void cancelOrderWhenInPendingState() {
        LocalDateTime now = LocalDateTime.now();

        Order order = createPendingOrder(now);
        order.cancel(now.plusMinutes(1));

        Assertions.assertEquals(OrderStatus.CANCELLED, order.getStatus());
        Assertions.assertTrue(order.isFinal());
    }
    @Test
    void rejectOrderWhenInProcessingState() {
        LocalDateTime now = LocalDateTime.now();

        Order order = createProcessingOrder(now);
        order.reject(now.plusMinutes(1));

        Assertions.assertEquals(OrderStatus.REJECTED, order.getStatus());
        Assertions.assertTrue(order.isFinal());
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
    void throwExceptionWhenFactoryReceivesInvalidData(){
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
    void gettersReturnCorrectValues() {
        LocalDateTime now = LocalDateTime.now();
        Order order = createPendingOrder(now);


        order.setId(1L);
        order.setUpdatedAt(now.plusMinutes(1));

        Assertions.assertNotNull(order.getId());
        Assertions.assertNotNull(order.getUser());
        Assertions.assertNotNull(order.getCreatedAt());
        Assertions.assertNotNull(order.getUpdatedAt());

        Assertions.assertEquals(BigDecimal.valueOf(100), order.getAmount());
        Assertions.assertEquals(OrderStatus.PENDING, order.getStatus());
    }



}