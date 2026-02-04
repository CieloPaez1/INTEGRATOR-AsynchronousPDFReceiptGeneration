package output;

import model.PendingTask;

public interface PdfOutput {
    void generate(PendingTask task);
}
