package usecase;

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

    private User validUser() {
        LocalDateTime now = LocalDateTime.now();
        User user = User.factory("john@example.com", "secret123", now);
        user.activate(now.plusMinutes(1));
        return user;
    }

    @Test
    void shouldCreateOrderSuccessfully() {
        Clock clock = fixedClock();
        User user = validUser();

        when(orderOutput.findUserById(1L)).thenReturn(user);
        when(orderOutput.saveOrder(any(Order.class))).thenReturn(true);

        CreateOrder useCase = new CreateOrder(orderOutput, clock);

        useCase.createOrder(1L, new BigDecimal("100.00"));

        verify(orderOutput).findUserById(1L);
        verify(orderOutput).saveOrder(any(Order.class));
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
        User user = validUser();

        when(orderOutput.findUserById(1L)).thenReturn(user);
        when(orderOutput.saveOrder(any(Order.class))).thenReturn(false);

        CreateOrder useCase = new CreateOrder(orderOutput, clock);

        Assertions.assertThrows(OrderException.class, () ->
                useCase.createOrder(1L, new BigDecimal("100.00"))
        );

        verify(orderOutput).findUserById(1L);
        verify(orderOutput).saveOrder(any(Order.class));
    }
    @Test
    void shouldFailWhenUserIsNotActive() {
        Clock clock = fixedClock();

        User pending = User.factory(
                "john@example.com",
                "secret123",
                LocalDateTime.now()
        ); // queda PENDING

        when(orderOutput.findUserById(1L)).thenReturn(pending);

        CreateOrder useCase = new CreateOrder(orderOutput, clock);

        Assertions.assertThrows(OrderException.class, () ->
                useCase.createOrder(1L, new BigDecimal("100.00"))
        );

        verify(orderOutput).findUserById(1L);
        verify(orderOutput, never()).saveOrder(any());
    }
    @Test
    void shouldFailWhenAmountIsZeroOrNegative() {
        Clock clock = fixedClock();
        User user = validUser();

        when(orderOutput.findUserById(1L)).thenReturn(user);

        CreateOrder useCase = new CreateOrder(orderOutput, clock);

        Assertions.assertThrows(OrderException.class, () ->
                useCase.createOrder(1L, BigDecimal.ZERO)
        );

        Assertions.assertThrows(OrderException.class, () ->
                useCase.createOrder(1L, new BigDecimal("-10"))
        );
    }



}