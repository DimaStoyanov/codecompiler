package ru.tsystems.tchallenge.codemaster.domain.models;

import lombok.Data;
import org.springframework.data.annotation.Id;

@Data
class AbstractDocument {
    @Id
    private String id;
}
