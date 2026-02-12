package output;

import model.PendingTask;

public interface PdfOutput {
    byte[] generate(PendingTask task);
}
