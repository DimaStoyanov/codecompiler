package tsystems.tchallenge.codecompiler.domain.models;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(exclude = "cmpErr")
@Builder
@Document(collection = "code-submissions")
public class CodeCompilationResult extends AbstractDocument{
    private CodeLanguage language;
    private CodeCompilationStatus status;
    private String cmpErr;
    private String workDirName;
}
