package usecase;

import enums.OrderStatus;
import exception.OrderException;
import model.Order;
import model.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import output.OrderOutput;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CreateOrderTest {

    @Mock
    private OrderOutput orderOutput;

    private Clock fixedClock() {
        return Clock.fixed(
                Instant.parse("2026-01-15T10:00:00Z"),
                ZoneId.systemDefault()
        );
    }

    private LocalDateTime now(Clock clock) {
        return LocalDateTime.ofInstant(clock.instant(), ZoneId.systemDefault());
    }

    private User validUser(Clock clock) {
        User user = User.factory(
                "john@example.com",
                "secret123",
                now(clock).minusHours(1)
        );
        user.activate(now(clock));
        return user;
    }

    private Order pendingOrder(User user, Clock clock) {
        return Order.factory(user, BigDecimal.valueOf(100), now(clock));
    }

    // ================= CREATE ORDER =================

    @Test
    void shouldCreateOrderSuccessfully() {
        Clock clock = fixedClock();
        User user = validUser(clock);

        when(orderOutput.findUserById(1L)).thenReturn(user);
        when(orderOutput.saveOrder(any(Order.class))).thenReturn(true);

        CreateOrder useCase = new CreateOrder(orderOutput, clock);

        useCase.createOrder(1L, new BigDecimal("100.00"));

        verify(orderOutput).findUserById(1L);
        verify(orderOutput).saveOrder(any(Order.class));
        verifyNoMoreInteractions(orderOutput);
    }

    @Test
    void shouldFailWhenUserNotFound() {
        Clock clock = fixedClock();

        when(orderOutput.findUserById(1L)).thenReturn(null);

        CreateOrder useCase = new CreateOrder(orderOutput, clock);

        Assertions.assertThrows(OrderException.class, () ->
                useCase.createOrder(1L, new BigDecimal("100.00"))
        );

        verify(orderOutput).findUserById(1L);
        verify(orderOutput, never()).saveOrder(any());
    }

    @Test
    void shouldFailWhenSaveOrderReturnsFalse() {
        Clock clock = fixedClock();
        User user = validUser(clock);

        when(orderOutput.findUserById(1L)).thenReturn(user);
        when(orderOutput.saveOrder(any(Order.class))).thenReturn(false);

        CreateOrder useCase = new CreateOrder(orderOutput, clock);

        Assertions.assertThrows(OrderException.class, () ->
                useCase.createOrder(1L, new BigDecimal("100.00"))
        );

        verify(orderOutput).saveOrder(any(Order.class));
    }

    @Test
    void shouldFailWhenUserIsNotActive() {
        Clock clock = fixedClock();

        User pending = User.factory(
                "john@example.com",
                "secret123",
                now(clock).minusHours(1)
        ); // queda PENDING

        when(orderOutput.findUserById(1L)).thenReturn(pending);

        CreateOrder useCase = new CreateOrder(orderOutput, clock);

        Assertions.assertThrows(OrderException.class, () ->
                useCase.createOrder(1L, new BigDecimal("100.00"))
        );

        verify(orderOutput, never()).saveOrder(any());
    }

    @Test
    void shouldFailWhenAmountIsZeroOrNegative() {
        Clock clock = fixedClock();
        User user = validUser(clock);

        when(orderOutput.findUserById(1L)).thenReturn(user);

        CreateOrder useCase = new CreateOrder(orderOutput, clock);

        Assertions.assertThrows(OrderException.class, () ->
                useCase.createOrder(1L, BigDecimal.ZERO)
        );

        Assertions.assertThrows(OrderException.class, () ->
                useCase.createOrder(1L, new BigDecimal("-10"))
        );
    }


    @Test
    void shouldChangeStateSuccessfully() {
        Clock clock = fixedClock();
        User user = validUser(clock);
        Order order = pendingOrder(user, clock);

        when(orderOutput.findById(1L)).thenReturn(order);
        when(orderOutput.saveOrder(order)).thenReturn(true);

        CreateOrder useCase = new CreateOrder(orderOutput, clock);

        boolean result = useCase.stateChange(1L, OrderStatus.PROCESSING);

        Assertions.assertTrue(result);
        Assertions.assertEquals(OrderStatus.PROCESSING, order.getStatus());
        verify(orderOutput).saveOrder(order);
    }

    @Test
    void shouldFailWhenOrderNotFoundInStateChange() {
        Clock clock = fixedClock();

        when(orderOutput.findById(1L)).thenReturn(null);

        CreateOrder useCase = new CreateOrder(orderOutput, clock);

        Assertions.assertThrows(OrderException.class, () ->
                useCase.stateChange(1L, OrderStatus.PROCESSING)
        );
    }

    @Test
    void shouldFailWhenStatusIsNull() {
        Clock clock = fixedClock();
        User user = validUser(clock);
        Order order = pendingOrder(user, clock);

        when(orderOutput.findById(1L)).thenReturn(order);

        CreateOrder useCase = new CreateOrder(orderOutput, clock);

        Assertions.assertThrows(OrderException.class, () ->
                useCase.stateChange(1L, null)
        );
    }

    @Test
    void shouldFailWhenTransitionIsInvalid() {
        Clock clock = fixedClock();
        User user = validUser(clock);
        Order order = pendingOrder(user, clock);

        when(orderOutput.findById(1L)).thenReturn(order);

        CreateOrder useCase = new CreateOrder(orderOutput, clock);

        Assertions.assertThrows(OrderException.class, () ->
                useCase.stateChange(1L, OrderStatus.APPROVED)
        );
    }
}