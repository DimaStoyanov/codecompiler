package tsystems.tchallenge.contest.api.dto;

import lombok.Builder;
import lombok.Data;
import tsystems.tchallenge.contest.domain.models.CodeLanguage;

@Data
@Builder
public class CodeLanguageInfo {
    private CodeLanguage language;
    private String languageName;
    private String sourceFileExt;
    private String compiledFileExt;
    private String notes;
}
