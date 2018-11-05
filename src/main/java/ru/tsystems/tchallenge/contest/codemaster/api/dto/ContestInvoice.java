package ru.tsystems.tchallenge.contest.codemaster.api.dto;

import lombok.Data;
import ru.tsystems.tchallenge.contest.codemaster.utils.data.ValidationAware;

import java.util.List;

@Data
public class ContestInvoice implements ValidationAware {
    private String name;
    private List<TestInvoice> tests;
    private Long timeLimit;
    private Long memoryLimit;


    @Override
    public void validate() {

    }
}
