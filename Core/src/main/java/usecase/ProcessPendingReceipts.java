package usecase;

import input.ProcessPendingReceiptsInput;
import model.PendingTask;
import output.PdfOutput;
import output.PendingTaskOutput;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;

public class ProcessPendingReceipts implements ProcessPendingReceiptsInput {
    private final PendingTaskOutput pendingTaskOutput;
    private final PdfOutput pdfOutput;
    private final Clock clock;

    public ProcessPendingReceipts(PendingTaskOutput pendingTaskOutput, PdfOutput pdfOutput, Clock clock) {
        this.pendingTaskOutput = pendingTaskOutput;
        this.pdfOutput = pdfOutput;
        this.clock = clock;
    }


    @Override
    public void process() {
        List<PendingTask> tasks = pendingTaskOutput.findAllPending();
        LocalDateTime now = LocalDateTime.now(clock);
        for (PendingTask task : tasks) {
            try {
                pdfOutput.generate(task);
                task.markDone(now);
            } catch (Exception e) {
                task.markError(now);
            }
            pendingTaskOutput.update(task);
        }
    }
}
