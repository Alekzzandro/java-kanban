package exception;

public class ManagerLoadFileException extends Exception {
    public ManagerLoadFileException(String message) {
        super(message);
    }

    public ManagerLoadFileException(String message, Throwable cause) {
        super(message, cause);
    }
}