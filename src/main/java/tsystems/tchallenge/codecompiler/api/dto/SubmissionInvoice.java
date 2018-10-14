package tsystems.tchallenge.codecompiler.api.dto;

import lombok.Data;
import tsystems.tchallenge.codecompiler.utils.ValidationAware;

@Data
public class SubmissionInvoice implements ValidationAware {

    private CompileSubmissionInvoice submission;
    private String contestId;

    @Override
    public void validate() {
        submission.validate();
    }
}
