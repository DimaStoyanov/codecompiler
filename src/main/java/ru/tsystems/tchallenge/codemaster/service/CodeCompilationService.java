package ru.tsystems.tchallenge.codemaster.service;


import ru.tsystems.tchallenge.codemaster.api.model.CompileInvoice;
import ru.tsystems.tchallenge.codemaster.api.model.CompileResult;

public interface CodeCompilationService {
    CompileResult compileFile(CompileInvoice invoice);

    CompileResult getCompilationResult(String id);
}
