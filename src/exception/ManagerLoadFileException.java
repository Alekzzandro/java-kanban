package exception;

public class ManagerLoadFileException extends RuntimeException {
    public ManagerLoadFileException(String message) {
        super(message);
    }

    public ManagerLoadFileException(String message, Throwable cause) {
        super(message, cause);
    }
}