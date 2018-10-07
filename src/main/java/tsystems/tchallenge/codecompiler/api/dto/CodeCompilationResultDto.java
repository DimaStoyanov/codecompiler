package tsystems.tchallenge.codecompiler.api.dto;

import lombok.Data;
import tsystems.tchallenge.codecompiler.domain.models.CodeCompilationStatus;

@Data
public class CodeCompilationResultDto {
    private String id;
    private CodeCompilationStatus status;
    private String cmpErr;
}
