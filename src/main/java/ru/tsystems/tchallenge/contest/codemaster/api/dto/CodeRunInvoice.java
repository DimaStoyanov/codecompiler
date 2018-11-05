package ru.tsystems.tchallenge.contest.codemaster.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.tsystems.tchallenge.contest.codemaster.utils.data.ValidationAware;

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
