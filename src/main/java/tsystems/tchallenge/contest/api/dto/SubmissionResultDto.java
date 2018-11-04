package tsystems.tchallenge.contest.api.dto;

import lombok.Data;
import tsystems.tchallenge.contest.domain.models.CodeLanguage;
import tsystems.tchallenge.contest.domain.models.SubmissionStatus;

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
