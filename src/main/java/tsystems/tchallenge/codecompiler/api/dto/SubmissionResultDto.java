package tsystems.tchallenge.codecompiler.api.dto;

import lombok.Data;
import tsystems.tchallenge.codecompiler.domain.models.CodeLanguage;
import tsystems.tchallenge.codecompiler.domain.models.SubmissionStatus;

import java.util.List;

@Data
public class SubmissionResultDto {
    private SubmissionStatus status;
    private CodeLanguage language;
    private String languageName;
    private Integer testNumber;
    private String cmpErr;
    private List<Long> memory;
    private List<Long> time;
    private String sourceCode;
}
