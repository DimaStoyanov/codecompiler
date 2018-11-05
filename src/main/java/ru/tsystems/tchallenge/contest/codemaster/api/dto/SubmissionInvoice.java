package ru.tsystems.tchallenge.contest.codemaster.api.dto;

import lombok.Data;
import ru.tsystems.tchallenge.contest.codemaster.utils.data.ValidationAware;

@Data
public class SubmissionInvoice implements ValidationAware {

    private CompileSubmissionInvoice submission;
    private String contestId;

    @Override
    public void validate() {
        submission.validate();
    }
}
