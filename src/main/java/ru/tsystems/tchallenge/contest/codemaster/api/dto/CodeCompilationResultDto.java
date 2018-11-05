package ru.tsystems.tchallenge.contest.codemaster.api.dto;

import lombok.Data;
import ru.tsystems.tchallenge.contest.codemaster.domain.models.CodeCompilationStatus;
import ru.tsystems.tchallenge.contest.codemaster.domain.models.CodeLanguage;

@Data
public class CodeCompilationResultDto {
    private String id;
    private CodeLanguage language;
    private String languageName;
    private CodeCompilationStatus status;
    private String cmpErr;
}
