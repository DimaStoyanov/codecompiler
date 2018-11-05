package ru.tsystems.tchallenge.codemaster.service.bean;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import ru.tsystems.tchallenge.codemaster.api.model.*;
import ru.tsystems.tchallenge.codemaster.domain.models.ContestEntity;
import ru.tsystems.tchallenge.codemaster.domain.models.SubmissionResultEntity;
import ru.tsystems.tchallenge.codemaster.domain.models.SubmissionStatus;
import ru.tsystems.tchallenge.codemaster.domain.models.Test;
import ru.tsystems.tchallenge.codemaster.domain.repositories.SubmissionResultRepository;
import ru.tsystems.tchallenge.codemaster.service.CodeCompilationService;
import ru.tsystems.tchallenge.codemaster.service.CodeRunService;
import ru.tsystems.tchallenge.codemaster.service.SubmissionMatcherService;
import ru.tsystems.tchallenge.codemaster.service.SubmissionService;
import ru.tsystems.tchallenge.codemaster.service.converter.SubmissionStatusConverter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import static java.lang.System.currentTimeMillis;
import static ru.tsystems.tchallenge.codemaster.reliability.OperationExceptionBuilder.internal;

@RequiredArgsConstructor
@Service
@Log4j2
public class SubmissionServiceBean implements SubmissionService {
    private final CodeCompilationService codeCompilationService;
    private final CodeRunService codeRunService;
    private final SubmissionResultRepository submissionResultRepository;
    private final SubmissionStatusConverter submissionStatusConverter;
    private final SubmissionMatcherService submissionMatcherService;

    private static final Integer WAIT_THREADS_LIMIT = 10;


    @Async
    public void runTests(ContestEntity contest, CompileInvoice invoice, SubmissionResultEntity result,
                         ExecutorService executorService) {
        long startTime = currentTimeMillis();
        var codeCompilationResult = compile(result, invoice);
        if (codeCompilationResult.getStatus() != CompileStatus.OK) {
            setCmpErr(codeCompilationResult, result);
            return;
        }

        List<Test> tests = contest.getTests();
        prepareResult(result, tests);

        for (int i = 0; i < tests.size(); i++) {
            // if some test failed, end work
            if (!isRunningStatus(result.getStatus())) {
                return;
            }
            final int testNumber = i;
            executorService.execute(() -> processTest(result, testNumber, tests,
                    codeCompilationResult.getId(), contest, executorService));

        }

        try {
            executorService.shutdown();
            executorService.awaitTermination(WAIT_THREADS_LIMIT, TimeUnit.MINUTES);
            if (!isRunningStatus(result.getStatus())) {
                return;
            }
        } catch (InterruptedException e) {
            throw internal(e);
        }


        result.setStatus(SubmissionStatus.OK);
        submissionResultRepository.save(result);
        long delta = currentTimeMillis() - startTime;
        log.info(String.format("Test run task finished successfully after %ds %dms", delta / 1000, delta % 1000));
        log.info(result);
    }

    private void prepareResult(SubmissionResultEntity result, List<Test> tests) {
        result.setTime(new ArrayList<>());
        tests.forEach(t -> {
            result.getTime().add(null);
        });
    }

    private CompileResult compile(SubmissionResultEntity result, CompileInvoice invoice) {
        result.setStatus(SubmissionStatus.COMPILING);
        var codeCompilationResult = codeCompilationService.compileFile(invoice);
        result.setCompileTaskId(codeCompilationResult.getId());
        submissionResultRepository.save(result);
        log.info(result);
        return codeCompilationResult;
    }


    private void setCmpErr(CompileResult codeCompilationResult, SubmissionResultEntity result) {
        result.setStatus(SubmissionStatus.COMPILATION_ERROR);
        result.setCmpErr(codeCompilationResult.getCmpErr());
        submissionResultRepository.save(result);
        log.info(result);
    }

    private void processTest(SubmissionResultEntity result, int testNumber, List<Test> tests,
                             String compilationId, ContestEntity contest, ExecutorService executorService) {
        updateRunTestStatus(result, testNumber);
        Test test = tests.get(testNumber);
        var runInvoice = buildRunInvoice(compilationId, test.getInput(), contest);
        var codeRunResult = codeRunService.runCode(runInvoice);
        if (!passTest(testNumber, codeRunResult, result, test.getOutput())) {
            submissionResultRepository.save(result);
            log.info(result);
            executorService.shutdownNow();
        }
    }


    private void updateRunTestStatus(SubmissionResultEntity result, int testNumber) {
        // If some test fails, no need to shallow it status
        if (!isRunningStatus(result.getStatus())) {
            Thread.currentThread().interrupt();
            return;
        }
        if (result.getTestNumber() != null && testNumber < result.getTestNumber()) {
            return;
        }

        result.setStatus(SubmissionStatus.RUNNING_TEST);
        result.setTestNumber(testNumber + 1);
        submissionResultRepository.save(result);
        log.info(result);
    }

    private boolean isRunningStatus(SubmissionStatus status) {
        return status == SubmissionStatus.RUNNING_TEST || status == SubmissionStatus.COMPILING;
    }

    private boolean passTest(int testNumber, RunResult codeRunResult, SubmissionResultEntity result, String expected) {
        var lastTestStatus = codeRunResult.getStatus();
        result.getTime().set(testNumber, codeRunResult.getTime());

        if (lastTestStatus != RunStatus.OK) {
            result.setStatus(submissionStatusConverter.fromCodeRunStatus(lastTestStatus));
            result.setTestNumber(testNumber + 1);
            return false;
        }

        if (!submissionMatcherService.matches(expected, codeRunResult.getOutput())) {
            result.setStatus(SubmissionStatus.WRONG_ANSWER);
            result.setTestNumber(testNumber + 1);
            return false;
        }

        return true;
    }

    private RunInvoice buildRunInvoice(String submissionId, String input, ContestEntity contest) {
        return new RunInvoice()
                .submissionId(submissionId)
                .input(input)
                .timeLimit(contest.getTimeLimit())
                .memoryLimit(contest.getMemoryLimit());
    }
}
