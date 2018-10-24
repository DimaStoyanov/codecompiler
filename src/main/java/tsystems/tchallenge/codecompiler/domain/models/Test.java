package tsystems.tchallenge.codecompiler.domain.models;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class Test implements Serializable {
    @ApiModelProperty("Program input")
    private String input;
    @ApiModelProperty("Expected program output")
    private String expectedOutput;
}
