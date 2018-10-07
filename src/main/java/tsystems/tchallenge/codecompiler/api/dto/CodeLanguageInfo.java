package tsystems.tchallenge.codecompiler.api.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Value;
import tsystems.tchallenge.codecompiler.domain.models.CodeLanguage;

@Data
@Builder
public class CodeLanguageInfo {
    private CodeLanguage language;
    private String languageName;
    private String sourceFileExt;
    private String compiledFileExt;
    private String notes;
}
