package ru.tsystems.tchallenge.codemaster.service;

import org.springframework.scheduling.annotation.Async;
import ru.tsystems.tchallenge.codemaster.api.model.CompileInvoice;
import ru.tsystems.tchallenge.codemaster.domain.models.ContestEntity;
import ru.tsystems.tchallenge.codemaster.domain.models.SubmissionResultEntity;

import java.util.concurrent.ExecutorService;

public interface SubmissionService {
    /**
     * Create async task, that run program n times and  check program output
     * @param contest Contest, that contains tests intput and output
     * @param invoice Invoice to compile & run program
     * @param result Result entity, which need to update
     * @param executorService async service, need new for each request
     */
    @Async
    void runTests(ContestEntity contest, CompileInvoice invoice, SubmissionResultEntity result, ExecutorService executorService);
}
