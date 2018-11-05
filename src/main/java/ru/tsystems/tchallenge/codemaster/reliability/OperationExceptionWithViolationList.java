package ru.tsystems.tchallenge.codemaster.reliability;


import ru.tsystems.tchallenge.codemaster.api.model.ViolationList;

public class OperationExceptionWithViolationList extends OperationException {

    private ViolationList violationList;

    public OperationExceptionWithViolationList(final ViolationList violationList) {
        this(null, violationList, null);
        this.violationList = violationList;
    }

    public OperationExceptionWithViolationList(final String description,
                                               final ViolationList violationList) {
        this(description, violationList, null);
        this.violationList = violationList;
    }

    public OperationExceptionWithViolationList(final String description,
                                               final ViolationList violationList,
                                               final Throwable cause) {
        super(OperationResultStatus.FAILURE_VALIDATION, description, cause);
        this.violationList = violationList;
    }

    public ViolationList getViolationList() {
        return violationList;
    }
}

