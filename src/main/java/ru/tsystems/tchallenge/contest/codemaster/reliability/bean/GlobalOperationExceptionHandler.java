package ru.tsystems.tchallenge.contest.codemaster.reliability.bean;

import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.tsystems.tchallenge.contest.codemaster.reliability.dto.OperationExceptionRepresentation;
import ru.tsystems.tchallenge.contest.codemaster.reliability.OperationException;

import javax.servlet.http.HttpServletRequest;
import java.time.Instant;

import static ru.tsystems.tchallenge.contest.codemaster.reliability.OperationExceptionType.ERR_INTERNAL;

@ControllerAdvice
@Log4j2
public class GlobalOperationExceptionHandler {

    @ExceptionHandler(OperationException.class)
    public ResponseEntity<Object> handleOperationException(OperationException exception,
                                                           HttpServletRequest request) {
        log.info(exception);
        HttpStatus status = null;


        OperationExceptionRepresentation dto = OperationExceptionRepresentation.builder()
                .description(exception.getDescription())
                .attachment(exception.getAttachment())
                .type(exception.getType())
                .error(status.getReasonPhrase())
                .path(request.getServletPath())
                .timestamp(Instant.now())
                .status(status.value())
                .build();

        if (exception.getType() == ERR_INTERNAL) {
            dto = dto.toInternal();
        }

        return new ResponseEntity<>(dto, status);
    }


}
