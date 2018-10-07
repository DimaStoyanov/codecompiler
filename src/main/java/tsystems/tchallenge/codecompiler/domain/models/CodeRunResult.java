package tsystems.tchallenge.codecompiler.domain.models;

import lombok.*;
import org.checkerframework.checker.units.qual.A;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Document(collection = "code-runs")
public class CodeRunResult extends AbstractDocument{
    private CodeLanguage language;
    private String languageName;
    private CodeRunStatus status;
    private String inputPath;
    private String input;
    private String outputPath;
    private String output;
    private String stderr;
    private Long memory;
    private String time;
}
