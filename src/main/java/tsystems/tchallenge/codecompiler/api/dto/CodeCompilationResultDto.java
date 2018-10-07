package tsystems.tchallenge.codecompiler.api.dto;

import lombok.Data;
import tsystems.tchallenge.codecompiler.domain.models.CodeCompilationStatus;
import tsystems.tchallenge.codecompiler.domain.models.CodeLanguage;

@Data
public class CodeCompilationResultDto {
    private String id;
    private CodeLanguage language;
    private String languageName;
    private CodeCompilationStatus status;
    private String cmpErr;
}
