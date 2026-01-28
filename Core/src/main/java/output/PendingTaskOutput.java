package output;

import model.PendingTask;


public interface PendingTaskOutput {
    boolean save(PendingTask task);
}
