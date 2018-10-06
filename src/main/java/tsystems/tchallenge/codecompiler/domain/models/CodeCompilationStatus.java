package tsystems.tchallenge.codecompiler.domain.models;

public enum CodeCompilationStatus {
    WAITING_IN_QUEUE,
    COMPILING,
    OK,
    COMPILATION_ERROR,
    SYSTEM_ERROR
}
