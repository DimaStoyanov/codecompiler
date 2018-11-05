package ru.tsystems.tchallenge.contest.codemaster.reliability;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.tsystems.tchallenge.contest.codemaster.utils.reflection.Reflections;

import java.util.function.Function;

import static ru.tsystems.tchallenge.contest.codemaster.utils.reflection.Reflections.instantiate;
import static ru.tsystems.tchallenge.contest.codemaster.utils.reflection.Reflections.invoke;

public class OperationResultResponseBuilder {

    private Object content;
    private String description;
    private OperationResultStatus status;
    private Function<OperationResultStatus, HttpStatus> statusMapper;
    private Class<?> type;
    private Long contentLength;

    public OperationResultResponseBuilder content(final Object content) {
        this.content = content;
        return this;
    }

    public OperationResultResponseBuilder description(final String description) {
        this.description = description;
        return this;
    }

    public OperationResultResponseBuilder status(final OperationResultStatus status) {
        this.status = status;
        return this;
    }

    public OperationResultResponseBuilder statusMapper(final Function<OperationResultStatus, HttpStatus> statusMapper) {
        this.statusMapper = statusMapper;
        return this;
    }

    public OperationResultResponseBuilder type(final Class<?> type) {
        this.type = type;
        return this;
    }

    public OperationResultResponseBuilder contentLength(final Long contentLength) {
        this.contentLength = contentLength;
        return this;
    }

    public <T> ResponseEntity<T> response() {
        final T body = operationResult();
        final HttpStatus httpStatus = statusMapper.apply(status);
        return new ResponseEntity<T>(body, httpStatus);
    }

    private <T> T operationResult() {
        final T operationResult = Reflections.cast(instantiate(type));
        if (content != null) {
            invoke("setContent", operationResult, content);
        }
        invoke("setDescription", operationResult, operationResultDescription());
        invoke("setStatusCode", operationResult, status.getCode());
        if (contentLength != null) {
            invoke("setContentLength", operationResult, contentLength);
        }
        return operationResult;
    }

    private String operationResultDescription() {
        return description != null ? description : status.getDefaultDescription();
    }
}