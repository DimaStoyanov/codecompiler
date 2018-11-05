package ru.tsystems.tchallenge.codemaster.service;

import ru.tsystems.tchallenge.codemaster.api.model.RunInvoice;
import ru.tsystems.tchallenge.codemaster.api.model.RunResult;

public interface CodeRunService {

    RunResult runCode(RunInvoice invoice);

    RunResult getResult(String id);
}
