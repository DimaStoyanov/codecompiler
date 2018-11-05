package ru.tsystems.tchallenge.contest.codemaster.domain.models;

import lombok.Data;
import org.springframework.data.annotation.Id;
import ru.tsystems.tchallenge.contest.codemaster.api.dto.IdAware;

@Data
class AbstractDocument implements IdAware {
    @Id
    private String id;
}
