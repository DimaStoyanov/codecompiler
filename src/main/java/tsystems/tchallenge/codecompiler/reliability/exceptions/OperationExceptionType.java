package tsystems.tchallenge.codecompiler.reliability.exceptions;

public enum OperationExceptionType {
    ERR_INTERNAL,
    // Compilation result with specified id not found
    ERR_COMPILATION_RESULT,
    // Run result with specified id not found
    ERR_RUN_RESULT,
    // Contest with specified id not found
    ERR_CONTEST,
    // Submission result not found
    ERR_SUBMISSION_RESULT
}
