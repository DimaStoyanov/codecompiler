package tsystems.tchallenge.codecompiler.reliability.exceptions;

import static tsystems.tchallenge.codecompiler.reliability.exceptions.OperationExceptionType.ERR_INTERNAL;

public class OperationExceptionBuilder {
    private OperationExceptionType type;
    private String description;
    private Object attachment;
    private Exception cause;

    private OperationExceptionBuilder() {

    }

    public static OperationExceptionBuilder builder() {
        return new OperationExceptionBuilder();
    }

    public static OperationException internal() {
        return internal(null, null);
    }

    public static OperationException internal(Object attachment) {
        return internal(attachment, null);
    }

    public static OperationException internal(Exception cause) {
        return internal(null, cause);
    }

    public static OperationException internal(Object attachment, Exception cause) {
        return new OperationException(ERR_INTERNAL, "Internal server error", attachment, cause);
    }

    public OperationExceptionBuilder type(OperationExceptionType type) {
        this.type = type;
        return this;
    }

    public OperationExceptionBuilder description(String description) {
        this.description = description;
        return this;
    }

    public OperationExceptionBuilder attachment(Object attachment) {
        this.attachment = attachment;
        return this;
    }

    public OperationExceptionBuilder cause(Exception cause) {
        this.cause = cause;
        return this;
    }

    public OperationException build() {
        return new OperationException(type, description, attachment, cause);
    }

}


