package ru.tsystems.tchallenge.codemaster.service.bean;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.stereotype.Service;
import ru.tsystems.tchallenge.codemaster.api.model.Language;
import ru.tsystems.tchallenge.codemaster.api.model.SubmissionInvoice;
import ru.tsystems.tchallenge.codemaster.api.model.SubmissionResult;
import ru.tsystems.tchallenge.codemaster.domain.models.CompileResultEntity;
import ru.tsystems.tchallenge.codemaster.domain.models.ContestEntity;
import ru.tsystems.tchallenge.codemaster.domain.models.SubmissionResultEntity;
import ru.tsystems.tchallenge.codemaster.domain.models.SubmissionStatus;
import ru.tsystems.tchallenge.codemaster.domain.repositories.CodeCompilationResultRepository;
import ru.tsystems.tchallenge.codemaster.domain.repositories.ContestRepository;
import ru.tsystems.tchallenge.codemaster.domain.repositories.SubmissionResultRepository;
import ru.tsystems.tchallenge.codemaster.reliability.OperationException;
import ru.tsystems.tchallenge.codemaster.reliability.OperationExceptionBuilder;
import ru.tsystems.tchallenge.codemaster.service.ResourceManager;
import ru.tsystems.tchallenge.codemaster.service.SubmissionResultService;
import ru.tsystems.tchallenge.codemaster.service.SubmissionService;
import ru.tsystems.tchallenge.codemaster.service.converter.SubmissionResultConverter;

import java.nio.file.Path;
import java.util.concurrent.ExecutorService;

import static ru.tsystems.tchallenge.codemaster.reliability.OperationResultStatus.FAILURE_LOGIC_RECORD_NOT_FOUND;

@RequiredArgsConstructor
@Log4j2
@Service
public class SubmissionResultServiceBean implements SubmissionResultService {
    private final ContestRepository contestRepository;
    private final SubmissionResultRepository submissionResultRepository;
    private final SubmissionService submissionService;
    private final SubmissionResultConverter submissionResultConverter;
    private final CodeCompilationResultRepository compilationResultRepository;
    private final ResourceManager resourceManager;

    @Lookup
    public ExecutorService getExecutorService() {
        return null;
    }

    @Override
    public SubmissionResult createSubmission(SubmissionInvoice invoice) {
        ContestEntity contest = contestRepository.findById(invoice.getContestId())
                .orElseThrow(() -> contestNotFound(invoice.getContestId()));
        SubmissionResultEntity result = SubmissionResultEntity.builder()
                .status(SubmissionStatus.WAITING_IN_QUEUE)
                .build();

        SubmissionResultEntity submissionResult = submissionResultRepository.save(result);
        log.info("Create submission with id "  + result.getId());
        submissionService.runTests(contest, invoice.getSubmission(), result, getExecutorService());
        return submissionResultConverter.toDto(submissionResult);
    }

    @Override
    public SubmissionResult getSubmission(String id, @NonNull Boolean withLang, @NonNull Boolean withSource) {
        return submissionResultRepository.findById(id)
                .map(r -> toDto(r, withLang, withSource))
                .orElseThrow(() -> submissionNotFound(id));
    }

    private SubmissionResult toDto(SubmissionResultEntity result, Boolean withLang, Boolean withSource) {
        var submissionResultDto = submissionResultConverter.toDto(result);

        if (withLang || withSource) {
            CompileResultEntity compilationResult = compilationResultRepository
                    .findById(result.getCompileTaskId())
                    .orElseThrow(() -> compilationResultNotFound(result.getCompileTaskId()));
            if (withLang) {
                submissionResultDto.setLanguage(Language.fromValue(compilationResult.getLanguage().name()));
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
                .type(FAILURE_LOGIC_RECORD_NOT_FOUND)
                .description("ContestEntity with specified id not found")
                .attachment(id)
                .build();
    }

    private OperationException submissionNotFound(String id) {
        return OperationExceptionBuilder.builder()
                .type(FAILURE_LOGIC_RECORD_NOT_FOUND)
                .description("Submission with specified id not found")
                .attachment(id)
                .build();
    }

    private OperationException compilationResultNotFound(String id) {
        return OperationExceptionBuilder.builder()
                .type(FAILURE_LOGIC_RECORD_NOT_FOUND)
                .description("Compilation result with specified id not found")
                .attachment(id)
                .build();
    }

}
