package ru.tsystems.tchallenge.codemaster.reliability;

import com.google.common.base.Strings;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
public class OperationException extends RuntimeException {
    private final OperationResultStatus status;
    private final String description;
    private Throwable cause;
    private Object attachment;


    public OperationException(OperationResultStatus status, String description) {
        super(description);
        this.description = description;
        this.status = status;
    }

    public OperationException(OperationResultStatus status, String description, Throwable cause) {
        super(description, cause);
        this.status = status;
        this.description = description;
        this.cause = cause;
    }

    @Override
    public String getMessage() {
        String description = Strings.isNullOrEmpty(this.description) ?
                "No description available" : this.description;
        if (attachment != null) {
            description += "\nAttachment: " + attachment;
        }
        return description;
    }

    @Override
    public synchronized Throwable getCause() {
        return cause;
    }
}
