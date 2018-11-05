package ru.tsystems.tchallenge.contest.codemaster.api.dto;

import ru.tsystems.tchallenge.contest.codemaster.domain.models.CodeLanguage;
import lombok.Data;
import ru.tsystems.tchallenge.contest.codemaster.utils.data.ValidationAware;

@Data
public class CompileSubmissionInvoice implements ValidationAware {
    private CodeLanguage language;
    private String sourceCode;

    @Override
    public void validate() {

    }
}
