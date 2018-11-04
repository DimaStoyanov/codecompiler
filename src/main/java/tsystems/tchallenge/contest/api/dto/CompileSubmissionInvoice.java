package tsystems.tchallenge.contest.api.dto;

import tsystems.tchallenge.contest.domain.models.CodeLanguage;
import lombok.Data;
import tsystems.tchallenge.contest.utils.ValidationAware;

@Data
public class CompileSubmissionInvoice implements ValidationAware {
    private CodeLanguage language;
    private String sourceCode;

    @Override
    public void validate() {

    }
}
