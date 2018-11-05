package ru.tsystems.tchallenge.contest.codemaster.api.dto;

import lombok.Data;
import ru.tsystems.tchallenge.contest.codemaster.domain.models.CodeLanguage;
import ru.tsystems.tchallenge.contest.codemaster.domain.models.CodeRunStatus;

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
