package usecase;

import enums.PendingTaskStatus;
import model.Order;
import model.PendingTask;
import model.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import output.PdfOutput;
import output.PendingTaskOutput;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProcessPendingReceiptsTest {

    @Mock
    private PendingTaskOutput pendingTaskOutput;

    @Mock
    private PdfOutput pdfOutput;

    private Clock fixedClock() {
        return Clock.fixed(
                Instant.parse("2026-01-15T10:00:00Z"),
                ZoneId.systemDefault()
        );
    }


    private ProcessPendingReceipts useCase(Clock clock) {
        return new ProcessPendingReceipts(pendingTaskOutput, pdfOutput, clock);
    }

    private PendingTask validTask(Clock clock) {
        LocalDateTime now = LocalDateTime.now(clock);

        User user = User.factory("john@example.com", "secret123", now);
        user.activate(now.plusMinutes(1));

        Order order = Order.factory(user, new BigDecimal("100.00"), now);

        return PendingTask.factory(order, now);
    }
    @Test
    void shouldMarkTaskAsDone() {
        Clock clock = fixedClock();
        PendingTask task = validTask(clock);

        when(pendingTaskOutput.findAllPending()).thenReturn(List.of(task));

        ProcessPendingReceipts useCase = useCase(clock);

        useCase.process();

        Assertions.assertEquals(PendingTaskStatus.DONE, task.getStatus());
        Assertions.assertEquals(LocalDateTime.now(clock), task.getProcessedAt());

        verify(pdfOutput).generate(task);
        verify(pendingTaskOutput).update(task);
    }
    @Test
    void shouldMarkTaskAsErrorWhenPdfFails() {
        Clock clock = fixedClock();
        PendingTask task = validTask(clock);

        when(pendingTaskOutput.findAllPending()).thenReturn(List.of(task));
        doThrow(new RuntimeException()).when(pdfOutput).generate(task);

        ProcessPendingReceipts useCase = useCase(clock);

        useCase.process();

        Assertions.assertEquals(PendingTaskStatus.ERROR, task.getStatus());
        Assertions.assertEquals(LocalDateTime.now(clock), task.getProcessedAt());

        verify(pdfOutput).generate(task);
        verify(pendingTaskOutput).update(task);
    }


    @Test
    void shouldSkipNonPendingTasks() {
        Clock clock = fixedClock();
        PendingTask task = validTask(clock);
        task.markDone(LocalDateTime.now(clock));
        when(pendingTaskOutput.findAllPending()).thenReturn(List.of(task));

        ProcessPendingReceipts useCase = useCase(clock);

        useCase.process();


        Assertions.assertEquals(PendingTaskStatus.DONE, task.getStatus());

        verifyNoInteractions(pdfOutput);
        verify(pendingTaskOutput, never()).update(any());
    }


}