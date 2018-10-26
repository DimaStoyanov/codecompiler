package tsystems.tchallenge.codecompiler.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tsystems.tchallenge.codecompiler.utils.ValidationAware;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CodeRunInvoice implements ValidationAware {
    private String submissionId;
    private String input;
    private Long executionTimeLimit;
    private Long memoryLimit;

    @Override
    public void validate() {
    }
}
