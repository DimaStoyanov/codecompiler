package tsystems.tchallenge.codecompiler.api.dto;

import lombok.Data;
import tsystems.tchallenge.codecompiler.domain.models.CodeLanguage;
import tsystems.tchallenge.codecompiler.domain.models.CodeRunStatus;

@Data
public class CodeRunResultDto {
    private String id;
    private CodeLanguage language;
    private String languageName;
    private CodeRunStatus status;
    private String input;
    private String output;
    private String stderr;
    private Long memory;
    private Long time;
}
