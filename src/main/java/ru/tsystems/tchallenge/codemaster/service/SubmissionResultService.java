package ru.tsystems.tchallenge.codemaster.service;

import lombok.NonNull;
import ru.tsystems.tchallenge.codemaster.api.model.SubmissionInvoice;
import ru.tsystems.tchallenge.codemaster.api.model.SubmissionResult;

public interface SubmissionResultService {
    SubmissionResult createSubmission(SubmissionInvoice invoice);

    SubmissionResult getSubmission(String id, @NonNull Boolean withLang, @NonNull Boolean withSource);
}
