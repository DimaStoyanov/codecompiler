package ru.tsystems.tchallenge.codemaster.reliability.bean;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.tsystems.tchallenge.codemaster.api.model.OperationResult;
import ru.tsystems.tchallenge.codemaster.api.model.OperationResultWithViolationList;
import ru.tsystems.tchallenge.codemaster.reliability.OperationException;
import ru.tsystems.tchallenge.codemaster.reliability.OperationExceptionWithViolationList;
import ru.tsystems.tchallenge.codemaster.reliability.OperationResultResponseBuilder;
import ru.tsystems.tchallenge.codemaster.reliability.OperationResultStatusMapper;

import static ru.tsystems.tchallenge.codemaster.reliability.OperationResultStatus.FAILURE_INTERNAL_UNKNOWN;
import static ru.tsystems.tchallenge.codemaster.reliability.OperationResultStatus.FAILURE_VALIDATION;

@ControllerAdvice
@RequiredArgsConstructor
@Log4j2
public class GlobalOperationExceptionHandler {

    private final OperationResultStatusMapper operationResultStatusMapper;

    @ExceptionHandler(TypeMismatchException.class)
    public ResponseEntity<?> handleTypeMismatchException(final TypeMismatchException exception) {
        log.warn(exception);
        return responseBuilder()
                .status(FAILURE_VALIDATION)
                .description(exception.getMessage())
                .response();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleInvalidArgumentException(final MethodArgumentNotValidException exception) {
        log.warn(exception.getMessage(), exception);
        return responseBuilder()
                .status(FAILURE_VALIDATION)
                .description(exception.getMessage())
                .response();
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<?> handleMessageNotReadableException(final HttpMessageNotReadableException exception) {
        log.warn(exception);
        return responseBuilder()
                .status(FAILURE_VALIDATION)
                .description(exception.getMessage())
                .response();
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<?> handleMethodNotSupportedException(final HttpRequestMethodNotSupportedException exception) {
        log.warn(exception.getMessage(), exception);
        return responseBuilder()
                .status(FAILURE_VALIDATION)
                .description(exception.getMessage())
                .response();
    }

    @ExceptionHandler(OperationException.class)
    public ResponseEntity<?> handleOperationException(final OperationException exception) {
        log.info(exception.getMessage(), exception);
        return responseBuilder()
                .status(exception.getStatus())
                .description(exception.getDescription())
                .response();
    }

    @ExceptionHandler(OperationExceptionWithViolationList.class)
    public ResponseEntity<?> handleOperationExceptionWithViolationList(final OperationExceptionWithViolationList exception) {
        log.info(exception.getMessage(), exception);
        return responseBuilder()
                .type(OperationResultWithViolationList.class)
                .status(exception.getStatus())
                .content(exception.getViolationList())
                .response();
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleException(final Exception exception) {
        log.error(exception.getMessage(), exception);
        // TODO: perform exception introspection
        return responseBuilder()
                .status(FAILURE_INTERNAL_UNKNOWN)
                .response();
    }

    private OperationResultResponseBuilder responseBuilder() {
        return new OperationResultResponseBuilder()
                .statusMapper(operationResultStatusMapper)
                .type(OperationResult.class);
    }

}
