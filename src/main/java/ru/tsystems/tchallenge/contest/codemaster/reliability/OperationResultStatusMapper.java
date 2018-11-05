package ru.tsystems.tchallenge.contest.codemaster.reliability;


import org.springframework.http.HttpStatus;

import java.util.function.Function;

public interface OperationResultStatusMapper extends Function<OperationResultStatus, HttpStatus> {
}
