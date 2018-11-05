package ru.tsystems.tchallenge.contest.codemaster.api.dto;

import lombok.Builder;
import lombok.Data;
import ru.tsystems.tchallenge.contest.codemaster.domain.models.CodeLanguage;

@Data
@Builder
public class CodeLanguageInfo {
    private CodeLanguage language;
    private String languageName;
    private String sourceFileExt;
    private String compiledFileExt;
    private String notes;
}
