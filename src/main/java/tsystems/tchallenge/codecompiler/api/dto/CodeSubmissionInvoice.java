package tsystems.tchallenge.codecompiler.api.dto;

import tsystems.tchallenge.codecompiler.domain.models.CodeLanguage;
import lombok.Data;

@Data
public class CodeSubmissionInvoice {
    private CodeLanguage language;
    private String sourceCode;
}
