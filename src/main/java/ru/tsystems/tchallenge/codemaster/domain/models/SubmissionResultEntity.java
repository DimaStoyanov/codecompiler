package ru.tsystems.tchallenge.codemaster.domain.models;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@ToString(exclude = "cmpErr")
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "submissions")
public class SubmissionResultEntity extends AbstractDocument{
    private SubmissionStatus status;
    private Integer testNumber;
    private String cmpErr;
    private List<Integer> time;
    private String compileTaskId;
}
