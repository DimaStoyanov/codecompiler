package tsystems.tchallenge.codecompiler.api.dto;

import tsystems.tchallenge.codecompiler.domain.models.CodeLanguage;
import lombok.Data;
import tsystems.tchallenge.codecompiler.utils.ValidationAware;

@Data
public class CompileSubmissionInvoice implements ValidationAware {
    private CodeLanguage language;
    private String sourceCode;

    @Override
    public void validate() {

    }
}
