package tsystems.tchallenge.contest.api.dto;

import lombok.Data;
import tsystems.tchallenge.contest.domain.models.CodeCompilationStatus;
import tsystems.tchallenge.contest.domain.models.CodeLanguage;

@Data
public class CodeCompilationResultDto {
    private String id;
    private CodeLanguage language;
    private String languageName;
    private CodeCompilationStatus status;
    private String cmpErr;
}
