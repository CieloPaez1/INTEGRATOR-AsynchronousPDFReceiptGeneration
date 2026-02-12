package persistence.PendingTask;

import com.cielo.adapter.persistence.pendingTask.PendingTaskEntity;
import com.cielo.adapter.persistence.pendingTask.PendingTaskJPARepository;
import com.cielo.adapter.persistence.pendingTask.PendingTaskRepository;
import model.Order;
import model.PendingTask;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PendingTaskRepositoryTest {
    @Mock
    private PendingTaskJPARepository jpa;

    @InjectMocks
    private PendingTaskRepository repository;

    @Test
    public void savePendingTaskSuccessfullyTest() {
        Order order = mock(Order.class);
        when(order.getId()).thenReturn(1L);

        PendingTask task = PendingTask.factory(order, LocalDateTime.now());

        PendingTaskEntity entity = new PendingTaskEntity();
        entity.setId(10L);

        when(jpa.save(any(PendingTaskEntity.class))).thenReturn(entity);

        boolean result = repository.save(task);

        Assertions.assertTrue(result);
        Assertions.assertEquals(10L, task.getId());
    }

    @Test
    public void savePendingTaskFailWhenIdNullTest() {
        Order order = mock(Order.class);
        when(order.getId()).thenReturn(1L);

        PendingTask task = PendingTask.factory(order, LocalDateTime.now());

        when(jpa.save(any(PendingTaskEntity.class)))
                .thenReturn(new PendingTaskEntity()); // id null

        boolean result = repository.save(task);

        Assertions.assertFalse(result);
    }

    @Test
    public void existsPendingForOrderTrueTest() {
        when(jpa.existsByOrderIdAndStatus(1L, enums.PendingTaskStatus.PENDING))
                .thenReturn(true);

        boolean result = repository.existsPendingForOrder(1L);

        Assertions.assertTrue(result);
    }

    @Test
    public void existsPendingForOrderFalseWhenNullTest() {
        boolean result = repository.existsPendingForOrder(null);
        Assertions.assertFalse(result);
    }
}

