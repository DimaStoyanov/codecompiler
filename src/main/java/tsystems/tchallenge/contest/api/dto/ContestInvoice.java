package tsystems.tchallenge.contest.api.dto;

import lombok.Data;
import tsystems.tchallenge.contest.utils.ValidationAware;

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
