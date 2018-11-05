package ru.tsystems.tchallenge.contest.codemaster.reliability.dto;

import lombok.Builder;
import lombok.Value;
import ru.tsystems.tchallenge.contest.codemaster.reliability.OperationExceptionType;

import java.time.Instant;

import static ru.tsystems.tchallenge.contest.codemaster.reliability.OperationExceptionType.ERR_INTERNAL;

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
