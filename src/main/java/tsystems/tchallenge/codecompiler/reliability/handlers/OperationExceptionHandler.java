package tsystems.tchallenge.codecompiler.reliability.handlers;

import lombok.extern.log4j.Log4j2;
import org.apache.catalina.loader.ResourceEntry;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import tsystems.tchallenge.codecompiler.reliability.dto.OperationExceptionRepresentation;
import tsystems.tchallenge.codecompiler.reliability.exceptions.OperationException;
import tsystems.tchallenge.codecompiler.reliability.exceptions.OperationExceptionType;

import javax.servlet.http.HttpServletRequest;
import java.time.Instant;

import static tsystems.tchallenge.codecompiler.reliability.exceptions.OperationExceptionType.ERR_INTERNAL;

@ControllerAdvice
@Log4j2
public class OperationExceptionHandler {

    @ExceptionHandler(OperationException.class)
    public ResponseEntity<Object> handleOperationException(OperationException exception,
                                                           HttpServletRequest request) {
        log.info(exception);
        HttpStatus status = exception.getStatus();


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
