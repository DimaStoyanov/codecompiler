package ru.tsystems.tchallenge.codemaster.utils.component;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import ru.tsystems.tchallenge.codemaster.api.model.Violation;
import ru.tsystems.tchallenge.codemaster.api.model.ViolationList;
import ru.tsystems.tchallenge.codemaster.reliability.OperationException;
import ru.tsystems.tchallenge.codemaster.reliability.OperationExceptionWithViolationList;
import ru.tsystems.tchallenge.codemaster.reliability.OperationResultStatus;
import ru.tsystems.tchallenge.codemaster.service.ErrorMessageService;

/**
 * Generic component implementation.
 */
@Slf4j
public abstract class GenericComponent {

    @Autowired
    private ErrorMessageService errorMessageService;

    /**
     * Operation exception runtime exception.
     *
     * @param status     the status
     * @param messageKey the message key
     * @return the runtime exception
     */
    protected RuntimeException operationException(final OperationResultStatus status, final String messageKey) {
        return new OperationException(status, errorMessageService.getLocalizedMessage(messageKey));
    }

    protected RuntimeException operationExceptionNotFound(final String messageKey) {
        return operationException(OperationResultStatus.FAILURE_LOGIC_RECORD_NOT_FOUND, messageKey);
    }

    protected RuntimeException operationException(final String messageKey) {
        return new OperationException(OperationResultStatus.FAILURE_INTERNAL_UNKNOWN, errorMessageService.getLocalizedMessage(messageKey));
    }


    protected RuntimeException operationException(final String message_header, final String message) {
        return new OperationException(OperationResultStatus.FAILURE_INTERNAL_UNKNOWN, message_header + message);
    }

    /**
     * Operation exception with violation list runtime exception.
     *
     * @param violationList the violation list
     * @return the runtime exception
     */
    protected RuntimeException operationExceptionWithViolationList(final ViolationList violationList) {
        violationList.forEach(violation -> {
            if (violation.getMessageKey() != null && violation.getMessageValue() == null)
                violation.setMessageValue(errorMessageService.getLocalizedMessage(violation.getMessageKey()));
        });
        return new OperationExceptionWithViolationList(violationList);
    }

    protected RuntimeException operationExceptionWithViolation(final Violation violation) {
        ViolationList violationList = new ViolationList();
        violationList.add(violation);
        return operationExceptionWithViolationList(violationList);
    }

    protected void logException(final Exception exception) {
        log.error("error: ", exception);
    }

}
