package tsystems.tchallenge.contest.domain.models;

import lombok.Data;
import org.springframework.data.annotation.Id;
import tsystems.tchallenge.contest.api.dto.IdAware;

@Data
class AbstractDocument implements IdAware {
    @Id
    private String id;
}
