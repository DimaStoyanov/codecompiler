package tsystems.tchallenge.codecompiler.reliability.handlers;

import org.apache.catalina.loader.ResourceEntry;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import tsystems.tchallenge.codecompiler.reliability.exceptions.OperationException;

@ControllerAdvice
public class OperationExceptionHandler {

    @ExceptionHandler(OperationException.class)
    public ResponseEntity<Object> handleOperationException(OperationException exception) {
        return new ResponseEntity<>(exception, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
