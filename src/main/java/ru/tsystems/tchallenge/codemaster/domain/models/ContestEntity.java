package ru.tsystems.tchallenge.codemaster.domain.models;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "contests")
@Data
@EqualsAndHashCode(callSuper = true)
public class ContestEntity extends AbstractDocument {
    private String name;
    private List<Test> tests;
    private Integer timeLimit;
    private Integer memoryLimit;
}
