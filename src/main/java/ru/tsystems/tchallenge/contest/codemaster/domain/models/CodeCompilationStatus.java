package ru.tsystems.tchallenge.contest.codemaster.domain.models;

public enum CodeCompilationStatus {
    WAITING_IN_QUEUE,
    COMPILING,
    OK,
    COMPILATION_ERROR,
    SYSTEM_ERROR
}
