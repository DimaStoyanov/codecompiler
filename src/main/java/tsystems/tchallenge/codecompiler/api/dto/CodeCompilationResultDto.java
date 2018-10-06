package tsystems.tchallenge.codecompiler.api.dto;

import tsystems.tchallenge.codecompiler.domain.models.CodeCompilationStatus;

public class CodeCompilationResultDto {
    private String id;
    private CodeCompilationStatus status;
    private String cmpErr;
}
