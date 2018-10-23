package tsystems.tchallenge.codecompiler.managers.contest;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tsystems.tchallenge.codecompiler.api.dto.IdAware;
import tsystems.tchallenge.codecompiler.api.dto.SubmissionInvoice;
import tsystems.tchallenge.codecompiler.api.dto.SubmissionResultDto;
import tsystems.tchallenge.codecompiler.converters.SubmissionResultConverter;
import tsystems.tchallenge.codecompiler.converters.SubmissionStatusConverter;
import tsystems.tchallenge.codecompiler.domain.models.CodeCompilationResult;
import tsystems.tchallenge.codecompiler.domain.models.Contest;
import tsystems.tchallenge.codecompiler.domain.models.SubmissionResult;
import tsystems.tchallenge.codecompiler.domain.models.SubmissionStatus;
import tsystems.tchallenge.codecompiler.domain.repositories.CodeCompilationResultRepository;
import tsystems.tchallenge.codecompiler.domain.repositories.ContestRepository;
import tsystems.tchallenge.codecompiler.domain.repositories.SubmissionResultRepository;
import tsystems.tchallenge.codecompiler.managers.compilation.CodeCompilationManager;
import tsystems.tchallenge.codecompiler.managers.resources.ResourceManager;
import tsystems.tchallenge.codecompiler.managers.running.CodeRunningManager;
import tsystems.tchallenge.codecompiler.reliability.exceptions.OperationException;
import tsystems.tchallenge.codecompiler.reliability.exceptions.OperationExceptionBuilder;
import tsystems.tchallenge.codecompiler.reliability.exceptions.OperationExceptionType;
import tsystems.tchallenge.codecompiler.utils.IdContainer;

import static tsystems.tchallenge.codecompiler.reliability.exceptions.OperationExceptionType.ERR_COMPILATION_RESULT;
import static tsystems.tchallenge.codecompiler.reliability.exceptions.OperationExceptionType.ERR_CONTEST;
import static tsystems.tchallenge.codecompiler.reliability.exceptions.OperationExceptionType.ERR_SUBMISSION_RESULT;

@RequiredArgsConstructor
@Service
public class SubmissionResultManager {

    private final ContestRepository contestRepository;
    private final SubmissionResultRepository submissionResultRepository;
    private final SubmissionManager submissionManager;
    private final SubmissionResultConverter submissionResultConverter;
    private final CodeCompilationResultRepository compilationResultRepository;
    private final ResourceManager resourceManager;

    public IdAware createSubmission(SubmissionInvoice invoice) {
        invoice.validate();
        Contest contest = contestRepository.findById(invoice.getContestId())
                .orElseThrow(() -> contestNotFound(invoice.getContestId()));
        SubmissionResult result = SubmissionResult.builder()
                .status(SubmissionStatus.WAITING_IN_QUEUE)
                .build();

        submissionResultRepository.save(result);
        submissionManager.runTests(contest, invoice.getSubmission(), result);
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
                String workDir = resourceManager.getWorkDirPath(compilationResult.getCompiledFilePath());
                submissionResultDto.setSourceCode(resourceManager.getSourceCode(workDir));
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
