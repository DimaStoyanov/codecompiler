package ru.tsystems.tchallenge.contest.codemaster.api.dto;

import lombok.Data;
import ru.tsystems.tchallenge.contest.codemaster.domain.models.CodeLanguage;
import ru.tsystems.tchallenge.contest.codemaster.domain.models.SubmissionStatus;

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
