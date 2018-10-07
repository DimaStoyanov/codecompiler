package tsystems.tchallenge.codecompiler.reliability.exceptions;

import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
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
        return new OperationException(ERR_INTERNAL, "Internal server error", attachment, cause,
                INTERNAL_SERVER_ERROR);
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
        HttpStatus status = null;
        switch (type) {
            case ERR_INTERNAL:
                status = INTERNAL_SERVER_ERROR;
                break;
            case ERR_COMPILATION_RESULT:
                status = BAD_REQUEST;
                break;
        }
        return new OperationException(type, description, attachment, cause, status);
    }

}


