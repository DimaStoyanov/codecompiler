package tsystems.tchallenge.codecompiler.managers.contest;

import lombok.RequiredArgsConstructor;
import tsystems.tchallenge.codecompiler.api.dto.IdAware;
import tsystems.tchallenge.codecompiler.api.dto.SubmissionInvoice;
import tsystems.tchallenge.codecompiler.api.dto.SubmissionResultDto;
import tsystems.tchallenge.codecompiler.converters.SubmissionResultConverter;
import tsystems.tchallenge.codecompiler.converters.SubmissionStatusConverter;
import tsystems.tchallenge.codecompiler.domain.models.Contest;
import tsystems.tchallenge.codecompiler.domain.models.SubmissionResult;
import tsystems.tchallenge.codecompiler.domain.models.SubmissionStatus;
import tsystems.tchallenge.codecompiler.domain.repositories.ContestRepository;
import tsystems.tchallenge.codecompiler.domain.repositories.SubmissionResultRepository;
import tsystems.tchallenge.codecompiler.managers.compilation.CodeCompilationManager;
import tsystems.tchallenge.codecompiler.managers.running.CodeRunningManager;
import tsystems.tchallenge.codecompiler.reliability.exceptions.OperationException;
import tsystems.tchallenge.codecompiler.reliability.exceptions.OperationExceptionBuilder;
import tsystems.tchallenge.codecompiler.reliability.exceptions.OperationExceptionType;
import tsystems.tchallenge.codecompiler.utils.IdContainer;

@RequiredArgsConstructor
public class SubmissionResultManager {

    private final ContestRepository contestRepository;
    private final SubmissionResultRepository submissionResultRepository;
    private final SubmissionManager submissionManager;
    private final SubmissionResultConverter submissionResultConverter;

    public IdAware createSubmission(SubmissionInvoice invoice) {
        invoice.validate();
        Contest contest = contestRepository.findById(invoice.getContestId())
                .orElseThrow(() -> contestNotFound(invoice.getContestId()));
        SubmissionResult result = SubmissionResult.builder()
                .status(SubmissionStatus.WAITING_IN_QUEUE)
                .build();

        submissionResultRepository.save(result);
        submissionManager.runTests(contest, invoice.getSubmission(), result );
        return  result.justId();
    }

    public SubmissionResultDto getResult(String id) {
        return submissionResultConverter.toDto(submissionResultRepository
                .findById(id)
                .orElseThrow(() -> submissionNotFound(id)));
    }


    private OperationException contestNotFound(String id) {
        return OperationExceptionBuilder.builder()
                .type(OperationExceptionType.ERR_CONTEST)
                .description("Contest with specified id not found")
                .attachment(id)
                .build();
    }

    private OperationException submissionNotFound(String id) {
        return OperationExceptionBuilder.builder()
                .type(OperationExceptionType.ERR_SUBMISSION_RESULT)
                .description("Submission with specified id not found")
                .attachment(id)
                .build();
    }
}
