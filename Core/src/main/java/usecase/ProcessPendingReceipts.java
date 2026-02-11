package usecase;

import input.ProcessPendingReceiptsInput;
import model.PendingTask;
import output.PdfOutput;
import output.PendingTaskOutput;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
    public List<byte[]> process() {
        List<PendingTask> tasks = pendingTaskOutput.findAllPending();
        List<byte[]> generatedPdfs = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now(clock);

        for (PendingTask task : tasks) {
            try {
                byte[] pdf = pdfOutput.generate(task);
                generatedPdfs.add(pdf);
                task.markDone(now);
            } catch (Exception e) {
                task.markError(now);
            }
            pendingTaskOutput.update(task);
        }
        return generatedPdfs;
    }
}
