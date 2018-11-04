package tsystems.tchallenge.contest.domain.models;

public enum SubmissionStatus {
    WAITING_IN_QUEUE, COMPILING, COMPILATION_ERROR, RUNNING_TEST, OK, SERVER_ERROR, RUNTIME_ERROR, TIME_LIMIT,
    MEMORY_LIMIT, WRONG_ANSWER
}
