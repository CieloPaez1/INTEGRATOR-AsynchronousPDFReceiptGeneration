package output;

import model.PendingTask;

import java.util.List;


public interface PendingTaskOutput {
    boolean save(PendingTask task);
    List<PendingTask> findAllPending();
    void update(PendingTask task);
}
