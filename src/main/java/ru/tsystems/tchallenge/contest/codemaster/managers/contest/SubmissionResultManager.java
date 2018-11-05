package ru.tsystems.tchallenge.contest.codemaster.managers.contest;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.stereotype.Service;
import ru.tsystems.tchallenge.contest.codemaster.api.dto.IdAware;
import ru.tsystems.tchallenge.contest.codemaster.api.dto.SubmissionInvoice;
import ru.tsystems.tchallenge.contest.codemaster.api.dto.SubmissionResultDto;
import ru.tsystems.tchallenge.contest.codemaster.converters.SubmissionResultConverter;
import ru.tsystems.tchallenge.contest.codemaster.domain.models.CodeCompilationResult;
import ru.tsystems.tchallenge.contest.codemaster.domain.models.Contest;
import ru.tsystems.tchallenge.contest.codemaster.domain.models.SubmissionResult;
import ru.tsystems.tchallenge.contest.codemaster.domain.models.SubmissionStatus;
import ru.tsystems.tchallenge.contest.codemaster.domain.repositories.CodeCompilationResultRepository;
import ru.tsystems.tchallenge.contest.codemaster.domain.repositories.ContestRepository;
import ru.tsystems.tchallenge.contest.codemaster.domain.repositories.SubmissionResultRepository;
import ru.tsystems.tchallenge.contest.codemaster.managers.resources.ServiceResourceManager;
import ru.tsystems.tchallenge.contest.codemaster.reliability.OperationException;
import ru.tsystems.tchallenge.contest.codemaster.reliability.OperationExceptionBuilder;

import java.nio.file.Path;
import java.util.concurrent.ExecutorService;

import static ru.tsystems.tchallenge.contest.codemaster.reliability.OperationExceptionType.*;

@RequiredArgsConstructor
@Service
@Log4j2
public class SubmissionResultManager {

    private final ContestRepository contestRepository;
    private final SubmissionResultRepository submissionResultRepository;
    private final SubmissionManager submissionManager;
    private final SubmissionResultConverter submissionResultConverter;
    private final CodeCompilationResultRepository compilationResultRepository;
    private final ServiceResourceManager resourceManager;

    @Lookup
    public ExecutorService getExecutorService() {
        return null;
    }

    public IdAware createSubmission(SubmissionInvoice invoice) {
        invoice.validate();
        Contest contest = contestRepository.findById(invoice.getContestId())
                .orElseThrow(() -> contestNotFound(invoice.getContestId()));
        SubmissionResult result = SubmissionResult.builder()
                .status(SubmissionStatus.WAITING_IN_QUEUE)
                .build();

        submissionResultRepository.save(result);
        log.info("Create submission with id "  + result.getId());
        submissionManager.runTests(contest, invoice.getSubmission(), result, getExecutorService());
        return result.justId();
    }

    public SubmissionResultDto getResult(String id, @NonNull Boolean withLang, @NonNull Boolean withSource) {
        return submissionResultRepository.findById(id)
                .map(r -> toDto(r, withLang, withSource))
                .orElseThrow(() -> submissionNotFound(id));
    }

    private SubmissionResultDto toDto(SubmissionResult result, Boolean withLang, Boolean withSource) {
        SubmissionResultDto submissionResultDto = submissionResultConverter.toDto(result);

        if (withLang || withSource) {
            CodeCompilationResult compilationResult = compilationResultRepository
                    .findById(result.getCompileTaskId())
                    .orElseThrow(() -> compilationResultNotFound(result.getCompileTaskId()));
            if (withLang) {
                submissionResultDto.setLanguage(compilationResult.getLanguage());
                submissionResultDto.setLanguageName(compilationResult.getLanguage().name);
            }
            if (withSource) {
                Path workDir = resourceManager.getWorkDir(compilationResult.getWorkDirName(), compilationResult.getLanguage());
                submissionResultDto.setSourceCode(resourceManager.readSourceCode(workDir));
            }
        }

        return submissionResultDto;
    }


    private OperationException contestNotFound(String id) {
        return OperationExceptionBuilder.builder()
                .type(ERR_CONTEST)
                .description("Contest with specified id not found")
                .attachment(id)
                .build();
    }

    private OperationException submissionNotFound(String id) {
        return OperationExceptionBuilder.builder()
                .type(ERR_SUBMISSION_RESULT)
                .description("Submission with specified id not found")
                .attachment(id)
                .build();
    }

    private OperationException compilationResultNotFound(String id) {
        return OperationExceptionBuilder.builder()
                .type(ERR_COMPILATION_RESULT)
                .description("Compilation result with specified id not found")
                .attachment(id)
                .build();
    }
}
