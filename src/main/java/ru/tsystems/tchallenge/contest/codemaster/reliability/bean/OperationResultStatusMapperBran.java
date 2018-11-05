package ru.tsystems.tchallenge.contest.codemaster.reliability.bean;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.tsystems.tchallenge.contest.codemaster.reliability.OperationResultStatus;
import ru.tsystems.tchallenge.contest.codemaster.reliability.OperationResultStatusMapper;

@Service
public class OperationResultStatusMapperBran implements OperationResultStatusMapper {
    @Override
    public HttpStatus apply(OperationResultStatus operationResultStatus) {
        return null;
    }
}
