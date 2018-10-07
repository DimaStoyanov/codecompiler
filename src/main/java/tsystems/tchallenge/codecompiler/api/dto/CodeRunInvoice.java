package tsystems.tchallenge.codecompiler.api.dto;

import lombok.Data;
import tsystems.tchallenge.codecompiler.utils.ValidationAware;

@Data
public class CodeRunInvoice implements ValidationAware {
    private String submissionId;
    private String input;

    @Override
    public void validate() {

    }
}
