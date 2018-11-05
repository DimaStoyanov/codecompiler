package ru.tsystems.tchallenge.codemaster.reliability;

public class OperationExceptionBuilder {
    private OperationResultStatus type;
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

    public static OperationException internal(Exception cause) {
        return internal(null, cause);
    }

    public static OperationException internal(Object attachment, Exception cause) {
        return new OperationException(OperationResultStatus.FAILURE_INTERNAL_UNKNOWN, "Internal server error", cause, attachment);
    }

    public OperationExceptionBuilder type(OperationResultStatus type) {
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
        return new OperationException(type, description, cause, attachment);
    }

}


