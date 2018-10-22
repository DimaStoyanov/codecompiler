package tsystems.tchallenge.codecompiler.domain.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "/submissions")
public class SubmissionResult extends AbstractDocument{
    private SubmissionStatus status;
    private Integer testNumber;
    private String cmpErr;
    private List<Long> memory;
    private List<Long> time;
}
