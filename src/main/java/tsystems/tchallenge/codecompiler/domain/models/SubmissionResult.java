package tsystems.tchallenge.codecompiler.domain.models;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "submissions")
public class SubmissionResult {
    private SubmissionStatus status;
    private Integer testNumber;
    private String cmpErr;
    private Long memory;
    private Long time;
}
