package usecase;

import exception.OrderException;
import model.Order;
import model.PendingTask;
import model.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import output.OrderOutput;
import output.PendingTaskOutput;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GenerateOrderReceiptPDFTest {

    @Mock
    private OrderOutput orderOutput;
    @Mock
    private PendingTaskOutput pendingTaskOutput;

    private Clock fixedClock() {
        return Clock.fixed(
                Instant.parse("2026-01-15T10:00:00Z"),
                ZoneId.systemDefault()
        );
    }
    private Order validOrder(Clock clock) {
        LocalDateTime now = LocalDateTime.now(clock);

        User user = User.factory("john@example.com", "secret123", now);
        user.activate(now.plusMinutes(5));

        return Order.factory(user, new BigDecimal("100.00"), now);
    }
    private GenerateOrderReceiptPDF useCase(Clock clock) {
        return new GenerateOrderReceiptPDF(orderOutput, pendingTaskOutput, clock);
    }
    @Test
    void shouldCreatePendingTask() {
        Clock clock = fixedClock();
        Order order = validOrder(clock);

        order.process(LocalDateTime.now(clock).plusMinutes(1));
        order.approve(LocalDateTime.now(clock).plusMinutes(2));

        when(orderOutput.findById(1L)).thenReturn(order);
        when(pendingTaskOutput.existsPendingForOrder(1L)).thenReturn(false);
        when(pendingTaskOutput.save(any(PendingTask.class))).thenReturn(true);

        GenerateOrderReceiptPDF useCase = useCase(clock);

        useCase.generateReceipt(1L);

        verify(orderOutput).findById(1L);
        verify(pendingTaskOutput).existsPendingForOrder(1L);
        verify(pendingTaskOutput).save(any(PendingTask.class));
    }

    @Test
    void shouldFailWhenOrderNotFound() {
        Clock clock = fixedClock();

        when(orderOutput.findById(1L)).thenReturn(null);

        GenerateOrderReceiptPDF useCase = useCase(clock);

        Assertions.assertThrows(OrderException.class, () ->
                useCase.generateReceipt(1L)
        );

        verify(orderOutput).findById(1L);
        verify(pendingTaskOutput, never()).existsPendingForOrder(any());
        verify(pendingTaskOutput, never()).save(any());
    }
    @Test
    void shouldFailWhenPendingTaskCannotBeSaved() {
        Clock clock = fixedClock();
        Order order = validOrder(clock);

        order.process(LocalDateTime.now(clock).plusMinutes(1));
        order.approve(LocalDateTime.now(clock).plusMinutes(2));

        when(orderOutput.findById(1L)).thenReturn(order);
        when(pendingTaskOutput.existsPendingForOrder(1L)).thenReturn(false);
        when(pendingTaskOutput.save(any(PendingTask.class))).thenReturn(false);

        GenerateOrderReceiptPDF useCase = useCase(clock);

        Assertions.assertThrows(OrderException.class, () ->
                useCase.generateReceipt(1L)
        );

        verify(pendingTaskOutput).existsPendingForOrder(1L);
        verify(pendingTaskOutput).save(any());
    }
    @Test
    void shouldFailWhenPendingTaskAlreadyExists() {
        Clock clock = fixedClock();
        Order order = validOrder(clock);

        order.process(LocalDateTime.now(clock).plusMinutes(1));
        order.approve(LocalDateTime.now(clock).plusMinutes(2));

        when(orderOutput.findById(1L)).thenReturn(order);
        when(pendingTaskOutput.existsPendingForOrder(1L)).thenReturn(true);

        GenerateOrderReceiptPDF useCase = useCase(clock);

        Assertions.assertThrows(OrderException.class, () ->
                useCase.generateReceipt(1L)
        );

        verify(pendingTaskOutput).existsPendingForOrder(1L);
        verify(pendingTaskOutput, never()).save(any());
    }
    @Test
    void shouldFailWhenOrderIsFinalButNotApproved() {
        Clock clock = fixedClock();
        Order order = validOrder(clock);

        order.process(LocalDateTime.now(clock).plusMinutes(1));
        order.reject(LocalDateTime.now(clock).plusMinutes(2));

        when(orderOutput.findById(1L)).thenReturn(order);

        GenerateOrderReceiptPDF useCase = useCase(clock);

        Assertions.assertThrows(OrderException.class, () ->
                useCase.generateReceipt(1L)
        );

        verify(orderOutput).findById(1L);
        verify(pendingTaskOutput, never()).existsPendingForOrder(any());
        verify(pendingTaskOutput, never()).save(any());
    }
    @Test
    void shouldFailWhenOrderIsNotFinal() {
        Clock clock = fixedClock();
        Order order = validOrder(clock);

        when(orderOutput.findById(1L)).thenReturn(order);

        GenerateOrderReceiptPDF useCase = useCase(clock);

        Assertions.assertThrows(OrderException.class, () ->
                useCase.generateReceipt(1L)
        );

        verify(orderOutput).findById(1L);
        verify(pendingTaskOutput, never()).existsPendingForOrder(any());
        verify(pendingTaskOutput, never()).save(any());
    }





}