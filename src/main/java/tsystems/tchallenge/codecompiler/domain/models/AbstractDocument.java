package tsystems.tchallenge.codecompiler.domain.models;

import lombok.Data;
import org.springframework.data.annotation.Id;
import tsystems.tchallenge.codecompiler.api.dto.IdAware;

@Data
class AbstractDocument implements IdAware {
    @Id
    private String id;
}
