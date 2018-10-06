package tsystems.tchallenge.codecompiler.reliability.exceptions;

public class OperationException extends RuntimeException {
    private OperationExceptionType type;
    private String details;
    private String name;
    private Object attachment;
    private Exception cause;


    @Override
    public String getMessage() {
        return details;
    }

    @Override
    public synchronized Throwable getCause() {
        return cause;
    }
}
