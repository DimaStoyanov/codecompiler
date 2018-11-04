package tsystems.tchallenge.contest.managers.contest;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import tsystems.tchallenge.contest.api.dto.CodeCompilationResultDto;
import tsystems.tchallenge.contest.api.dto.CodeRunInvoice;
import tsystems.tchallenge.contest.api.dto.CodeRunResultDto;
import tsystems.tchallenge.contest.api.dto.CompileSubmissionInvoice;
import tsystems.tchallenge.contest.converters.SubmissionStatusConverter;
import tsystems.tchallenge.contest.domain.models.*;
import tsystems.tchallenge.contest.domain.repositories.SubmissionResultRepository;
import tsystems.tchallenge.contest.managers.compilation.CodeCompilationManager;
import tsystems.tchallenge.contest.managers.running.CodeRunningManager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import static java.lang.System.currentTimeMillis;
import static tsystems.tchallenge.contest.reliability.exceptions.OperationExceptionBuilder.internal;

@Service
@RequiredArgsConstructor
@Log4j2
public class SubmissionManager {
    private final CodeCompilationManager codeCompilationManager;
    private final CodeRunningManager codeRunningManager;
    private final SubmissionResultRepository submissionResultRepository;
    private final SubmissionStatusConverter submissionStatusConverter;
    private final SubmissionMatcherManager submissionMatcherManager;



    @Async
    public void runTests(Contest contest, CompileSubmissionInvoice invoice, SubmissionResult result,
                         ExecutorService executorService) {
        long startTime = currentTimeMillis();
        CodeCompilationResultDto codeCompilationResult = compile(result, invoice);
        if (codeCompilationResult.getStatus() != CodeCompilationStatus.OK) {
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
            final  int testNumber = i;
            executorService.execute(() -> processTest(result, testNumber, tests,
                    codeCompilationResult.getId(), contest, executorService));

        }

        try {
            executorService.shutdown();
            // TODO: extract limit
            executorService.awaitTermination(1, TimeUnit.MINUTES);
            if (!isRunningStatus(result.getStatus())) {
                return;
            }
        } catch (InterruptedException e) {
            throw internal(e);
        }


        result.setStatus(SubmissionStatus.OK);
        submissionResultRepository.save(result);
        long delta = currentTimeMillis() - startTime;
        log.info(String.format("Test run task finished successfully after %ds %dms",  delta / 1000, delta % 1000));
        log.info(result);
    }

    private void prepareResult(SubmissionResult result, List<Test> tests) {
        result.setMemory(new ArrayList<>(tests.size()));
        result.setTime(new ArrayList<>());
        tests.forEach(t -> {
            result.getMemory().add(null);
            result.getTime().add(null);
        });
    }

    private CodeCompilationResultDto compile(SubmissionResult result, CompileSubmissionInvoice invoice) {
        result.setStatus(SubmissionStatus.COMPILING);
        CodeCompilationResultDto codeCompilationResult = codeCompilationManager.compileFile(invoice);
        result.setCompileTaskId(codeCompilationResult.getId());
        submissionResultRepository.save(result);
        log.info(result);
        return codeCompilationResult;
    }



    private void setCmpErr(CodeCompilationResultDto codeCompilationResult, SubmissionResult result) {
        result.setStatus(SubmissionStatus.COMPILATION_ERROR);
        result.setCmpErr(codeCompilationResult.getCmpErr());
        submissionResultRepository.save(result);
        log.info(result);
    }

    private void processTest(SubmissionResult result, int testNumber, List<Test> tests,
                             String compilationId, Contest contest, ExecutorService executorService) {
        updateRunTestStatus(result, testNumber);
        Test test = tests.get(testNumber);
        CodeRunInvoice runInvoice = buildRunInvoice(compilationId, test.getInput(), contest);
        CodeRunResultDto codeRunResult = codeRunningManager.runCode(runInvoice);
        if (!passTest(testNumber, codeRunResult, result, test.getExpectedOutput())) {
            submissionResultRepository.save(result);
            log.info(result);
            executorService.shutdownNow();
        }
    }


    private void updateRunTestStatus(SubmissionResult result, int testNumber) {
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

    private boolean passTest(int testNumber, CodeRunResultDto codeRunResult, SubmissionResult result, String expected) {
        CodeRunStatus lastTestStatus = codeRunResult.getStatus();
        result.getTime().set(testNumber, codeRunResult.getTime());
        result.getMemory().set(testNumber, codeRunResult.getMemory());

        if (lastTestStatus != CodeRunStatus.OK) {
            result.setStatus(submissionStatusConverter.fromCodeRunStatus(lastTestStatus));
            result.setTestNumber(testNumber + 1);
            return false;
        }

        if (!submissionMatcherManager.matches(expected, codeRunResult.getOutput())) {
            result.setStatus(SubmissionStatus.WRONG_ANSWER);
            result.setTestNumber(testNumber + 1);
            return false;
        }

        return true;
    }

    private CodeRunInvoice buildRunInvoice(String submissionId, String input, Contest contest) {
        return CodeRunInvoice.builder()
                .submissionId(submissionId)
                .input(input)
                .executionTimeLimit(contest.getTimeLimit())
                .memoryLimit(contest.getMemoryLimit())
                .build();
    }
}