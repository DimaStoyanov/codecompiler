package ru.tsystems.tchallenge.contest.codemaster.reliability;

public enum OperationResultStatus {


    FAILURE_VALIDATION(202, "Operation cannot be completed due to input validation failure"),

    FAILURE_LOGIC_RECORD_NOT_FOUND(309, "Operation cannot be completed due to missing of requested data"),

    FAILURE_INTERNAL_UNKNOWN(500, "Operation has been interrupted by an unexpected error"),

    SUCCESS_CREATED(101, "New data has been successfully created and persisted"),

    SUCCESS_UPDATED(102, "Requested data has been successfully updated and persisted"),

    SUCCESS_RETRIEVED(103, "Requested data has been successfully retrieved"),

    SUCCESS_DELETED(104, "Requested data has been successfully deleted");



    private final Integer code;
    private final String defaultDescription;

    OperationResultStatus(final Integer code, final String defaultDescription) {
        this.code = code;
        this.defaultDescription = defaultDescription;
    }

    public Integer getCode() {
        return code;
    }

    public String getDefaultDescription() {
        return defaultDescription;
    }
}
