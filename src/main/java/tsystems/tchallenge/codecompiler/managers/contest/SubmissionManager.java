package tsystems.tchallenge.codecompiler.managers.contest;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import tsystems.tchallenge.codecompiler.api.dto.CodeCompilationResultDto;
import tsystems.tchallenge.codecompiler.api.dto.CodeRunInvoice;
import tsystems.tchallenge.codecompiler.api.dto.CodeRunResultDto;
import tsystems.tchallenge.codecompiler.api.dto.CompileSubmissionInvoice;
import tsystems.tchallenge.codecompiler.converters.SubmissionStatusConverter;
import tsystems.tchallenge.codecompiler.domain.models.*;
import tsystems.tchallenge.codecompiler.domain.repositories.SubmissionResultRepository;
import tsystems.tchallenge.codecompiler.managers.compilation.CodeCompilationManager;
import tsystems.tchallenge.codecompiler.managers.running.CodeRunningManager;

import java.util.ArrayList;
import java.util.List;

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
    public void runTests(Contest contest, CompileSubmissionInvoice invoice, SubmissionResult result) {
        CodeCompilationResultDto codeCompilationResult = compile(result, invoice);
        if (codeCompilationResult.getStatus() != CodeCompilationStatus.OK) {
            setCmpErr(codeCompilationResult, result);
            return;
        }


        result.setMemory(new ArrayList<>());
        result.setTime(new ArrayList<>());
        List<Test> tests = contest.getTests();

        for (int testNumber = 0; testNumber < tests.size(); testNumber++) {
            updateRunTestStatus(result, testNumber);
            Test test = tests.get(testNumber);
            CodeRunInvoice runInvoice = buildRunInvoice(codeCompilationResult.getId(), test.getInput(), contest);
            CodeRunResultDto codeRunResult = codeRunningManager.runCode(runInvoice);
            if (!passTest(codeRunResult, result, test.getExpectedOutput())) {
                submissionResultRepository.save(result);
                log.info(result);
                return;
            }
        }

        result.setStatus(SubmissionStatus.OK);
        submissionResultRepository.save(result);
        log.info(result);
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


    private void updateRunTestStatus(SubmissionResult result, int testNumber) {
        result.setStatus(SubmissionStatus.RUNNING_TEST);
        result.setTestNumber(testNumber + 1);
        submissionResultRepository.save(result);
        log.info(result);
    }

    private boolean passTest(CodeRunResultDto codeRunResult, SubmissionResult result, String expected) {
        CodeRunStatus lastTestStatus = codeRunResult.getStatus();
        result.getTime().add(codeRunResult.getTime());
        result.getMemory().add(codeRunResult.getMemory());

        if (lastTestStatus != CodeRunStatus.OK) {
            result.setStatus(submissionStatusConverter.fromCodeRunStatus(lastTestStatus));
            return false;
        }

        if (!submissionMatcherManager.matches(expected, codeRunResult.getOutput())) {
            result.setStatus(SubmissionStatus.WRONG_ANSWER);
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