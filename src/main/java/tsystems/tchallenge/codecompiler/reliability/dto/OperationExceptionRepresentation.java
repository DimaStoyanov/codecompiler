package tsystems.tchallenge.codecompiler.reliability.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Value;
import tsystems.tchallenge.codecompiler.reliability.exceptions.OperationExceptionType;

import java.time.Instant;
import java.util.Date;

import static tsystems.tchallenge.codecompiler.reliability.exceptions.OperationExceptionType.ERR_INTERNAL;

@Value
@Builder
public class OperationExceptionRepresentation {
    private String description;
    private OperationExceptionType type;
    private Object attachment;
    private Integer status;
    private String path;
    private Instant timestamp;
    private String error;

    public OperationExceptionRepresentation toInternal() {
        return new OperationExceptionRepresentation(
                "Internal server error", ERR_INTERNAL,
                null, status, path, timestamp, error
        );
    }
}
