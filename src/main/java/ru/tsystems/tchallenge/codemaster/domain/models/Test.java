package ru.tsystems.tchallenge.codemaster.domain.models;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class Test implements Serializable {
    private String input;
    private String output;
}
