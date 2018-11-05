package ru.tsystems.tchallenge.codemaster.service.validator.bean;

import lombok.RequiredArgsConstructor;
import ru.tsystems.tchallenge.codemaster.api.model.Violation;
import ru.tsystems.tchallenge.codemaster.api.model.ViolationList;
import ru.tsystems.tchallenge.codemaster.reliability.OperationException;
import ru.tsystems.tchallenge.codemaster.reliability.OperationExceptionWithViolationList;
import ru.tsystems.tchallenge.codemaster.reliability.OperationResultStatus;
import ru.tsystems.tchallenge.codemaster.service.ErrorMessageService;
import ru.tsystems.tchallenge.codemaster.service.validator.GenericValidator;

import java.time.LocalDate;

@RequiredArgsConstructor
public abstract class GenericValidatorBean<D> implements GenericValidator<D> {

    private ViolationList violationList;
    private final ErrorMessageService messageService;
    @Override

    public void validateOnGet(final String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new OperationException(OperationResultStatus.FAILURE_LOGIC_RECORD_NOT_FOUND, messageService.getLocalizedMessage("get.id.incorrect"));
        }
    }

    @Override
    public void validateOnGetResult(final D existingObject) {
        mustExist(existingObject);
    }

    @Override
    public void validateOnCreate(final D invoiceObject) {

    }

    @Override
    public void validateOnUpdate(final D invoiceObject, final D existingObject) {
        mustExist(invoiceObject);
    }

    @Override
    public void validateOnDelete(final String idToDelete) {

    }

    private void mustExist(final D existingObject) {
        if (existingObject == null) {
            throw new OperationException(OperationResultStatus.FAILURE_LOGIC_RECORD_NOT_FOUND, messageService.getLocalizedMessage("not.found.by.id"));
        }
    }

    protected void registerViolationError(String property, String messageKey) {
        registerViolationError(new Violation().property(property).messageKey(messageKey));
    }

    protected void registerViolationError(String property, String messageKey, String messageValue) {
        registerViolationError(new Violation().property(property).messageKey(messageKey).messageValue(messageValue));
    }

    protected void registerViolationError(String property, String messageKey, Object errorObject) {
        registerViolationError(new Violation().property(property).messageKey(messageKey).data(errorObject));
    }

    protected void registerViolationError(final Violation violation) {
        synchronized (this) {
            if (violationList == null) {
                synchronized (this) {
                    violationList = new ViolationList();
                }
            }
        }
        if (violation != null && violationList.stream().noneMatch(existingViolation ->
                existingViolation.getProperty().equals(violation.getProperty()) &&
                        existingViolation.getMessageKey().equals(violation.getMessageKey())
        )) {
            violationList.add(violation);
        }
    }

    protected void throwViolationListExceptionIfExist() {
        if (violationList != null && violationList.size() > 0) {
            ViolationList violationListCopy = new ViolationList();
            violationListCopy.addAll(violationList);
            violationList.clear();
            throw new OperationExceptionWithViolationList(violationListCopy);
        }
    }

    protected void throwValidationException(final String messageKey) {
        throw new OperationException(OperationResultStatus.FAILURE_VALIDATION, messageService.getLocalizedMessage(messageKey));
    }

    protected void throwValidationException(final OperationResultStatus operationResultStatus, final String messageKey) {
        throw new OperationException(operationResultStatus, messageService.getLocalizedMessage(messageKey));
    }

    protected LocalDate endPrevMonthYear() {
        LocalDate now = LocalDate.now();
        return now.minusDays(now.getDayOfMonth());
    }

    protected LocalDate today() {
        return LocalDate.now();
    }
}
