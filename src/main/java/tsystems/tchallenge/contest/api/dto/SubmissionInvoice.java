package tsystems.tchallenge.contest.api.dto;

import lombok.Data;
import tsystems.tchallenge.contest.utils.ValidationAware;

@Data
public class SubmissionInvoice implements ValidationAware {

    private CompileSubmissionInvoice submission;
    private String contestId;

    @Override
    public void validate() {
        submission.validate();
    }
}
