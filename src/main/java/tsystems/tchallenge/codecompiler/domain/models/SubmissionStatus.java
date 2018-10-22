package tsystems.tchallenge.codecompiler.domain.models;

public enum SubmissionStatus {
    WAITING_IN_QUEUE, COMPILING, COMPILATION_ERROR, RUNNING_TEST, OK, SERVER_ERROR, TIME_LIMIT,
    MEMORY_LIMIT, WRONG_ANWER
}
