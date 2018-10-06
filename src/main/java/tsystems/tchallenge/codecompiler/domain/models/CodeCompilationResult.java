package tsystems.tchallenge.codecompiler.domain.models;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@Document(collection = "code-submissions")
public class CodeCompilationResult extends AbstractDocument{
    private CodeLanguage language;
    private String languageName;
    private CodeCompilationStatus status;
    private String cmpErr;
    private String compiledFilePath;
}
