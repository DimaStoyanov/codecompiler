package tsystems.tchallenge.codecompiler.domain.models;

import lombok.Data;
import tsystems.tchallenge.codecompiler.api.dto.IdAware;

@Data
public class AbstractDocument implements IdAware {
    private String id;
}
