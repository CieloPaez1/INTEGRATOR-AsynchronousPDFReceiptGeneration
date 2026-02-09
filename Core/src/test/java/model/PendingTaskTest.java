package model;

import enums.PendingTaskStatus;
import exception.PendingTaskException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PendingTaskTest {

    private User activeUser(LocalDateTime now) {
        User user = User.factory("john@example.com", "secret123", now);
        user.activate(now.plusMinutes(1));
        return user;
    }

    private Order pendingOrder(LocalDateTime now) {
        return Order.factory(activeUser(now), BigDecimal.valueOf(100), now);
    }
    @Test
    void factoryCreatesPendingTaskSuccessfully() {
        LocalDateTime now = LocalDateTime.now();
        Order order = pendingOrder(now);

        PendingTask task = PendingTask.factory(order, now);

        Assertions.assertNotNull(task);
        Assertions.assertEquals(PendingTaskStatus.PENDING, task.getStatus());
        Assertions.assertTrue(task.isPending());
        Assertions.assertNull(task.getProcessedAt());
    }

    @Test
    void factoryThrowsExceptionWhenOrderIsNull() {
        LocalDateTime now = LocalDateTime.now();

        Assertions.assertThrows(PendingTaskException.class, () ->
                PendingTask.factory(null, now)
        );
    }

    @Test
    void factoryThrowsExceptionWhenNowIsNull() {
        LocalDateTime now = LocalDateTime.now();
        Order order = pendingOrder(now);

        Assertions.assertThrows(PendingTaskException.class, () ->
                PendingTask.factory(order, null)
        );
    }

    @Test
    void markDoneChangesStatusAndSetsProcessedAt() {
        LocalDateTime now = LocalDateTime.now();
        PendingTask task = PendingTask.factory(pendingOrder(now), now);

        LocalDateTime processedTime = now.plusMinutes(5);
        task.markDone(processedTime);

        Assertions.assertEquals(PendingTaskStatus.DONE, task.getStatus());
        Assertions.assertEquals(processedTime, task.getProcessedAt());
        Assertions.assertFalse(task.isPending());
    }

    @Test
    void markDoneThrowsExceptionWhenNowIsNull() {
        LocalDateTime now = LocalDateTime.now();
        PendingTask task = PendingTask.factory(pendingOrder(now), now);

        Assertions.assertThrows(PendingTaskException.class, () ->
                task.markDone(null)
        );
    }

    @Test
    void markErrorChangesStatusAndSetsProcessedAt() {
        LocalDateTime now = LocalDateTime.now();
        PendingTask task = PendingTask.factory(pendingOrder(now), now);

        LocalDateTime processedTime = now.plusMinutes(3);
        task.markError(processedTime);

        Assertions.assertEquals(PendingTaskStatus.ERROR, task.getStatus());
        Assertions.assertEquals(processedTime, task.getProcessedAt());
        Assertions.assertFalse(task.isPending());
    }

    @Test
    void markErrorThrowsExceptionWhenNowIsNull() {
        LocalDateTime now = LocalDateTime.now();
        PendingTask task = PendingTask.factory(pendingOrder(now), now);

        Assertions.assertThrows(PendingTaskException.class, () ->
                task.markError(null)
        );
    }
    @Test
    void restoreRecreatesOrderWithGivenValues() {
        LocalDateTime now = LocalDateTime.now();

        Order order = pendingOrder(now);

        PendingTask task = PendingTask.restore(
                10L,
                order,
                PendingTaskStatus.DONE,
                now.minusHours(2),
                now
        );

        Assertions.assertEquals(10L, task.getId());
        Assertions.assertEquals(order, task.getOrder());
        Assertions.assertEquals(PendingTaskStatus.DONE, task.getStatus());
        Assertions.assertEquals(now.minusHours(2), task.getCreatedAt());
        Assertions.assertEquals(now, task.getProcessedAt());
    }


}

