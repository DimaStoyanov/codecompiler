package ru.tsystems.tchallenge.contest.codemaster.reliability;

import com.google.common.base.Strings;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class OperationException extends RuntimeException {
    private final OperationExceptionType type;
    private final String description;
    private final Object attachment;
    private final Exception cause;

    OperationException(OperationExceptionType type, String description,
                              Object attachment, Exception cause) {
        this.type = type;
        this.description = description;
        this.attachment = attachment;
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
