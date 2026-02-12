package exception;

public class PendingTaskException extends RuntimeException {
    public PendingTaskException(String msj) {
        super(msj);
    }
}
