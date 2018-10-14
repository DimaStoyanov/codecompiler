package tsystems.tchallenge.codecompiler.api.dto;

import io.swagger.annotations.ApiModelProperty;
import tsystems.tchallenge.codecompiler.utils.ValidationAware;

import java.util.List;

public class ContestInvoice implements ValidationAware {
    private String name;
    private List<TestInvoice> tests;
    private Long timeLimit;
    private Long memoryLimit;


    @Override
    public void validate() {

    }
}
