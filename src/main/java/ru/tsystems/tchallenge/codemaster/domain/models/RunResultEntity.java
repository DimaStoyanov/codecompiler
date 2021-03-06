package ru.tsystems.tchallenge.codemaster.domain.models;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "stderr")
@EqualsAndHashCode(callSuper = true)
@Document(collection = "code-runs")
public class RunResultEntity extends AbstractDocument{
    private CodeLanguage language;
    private String languageName;
    private CodeRunStatus status;
    private String compileSubmissionId;
    private String stderr;
    private String workDirName;
    private Long time;
}
