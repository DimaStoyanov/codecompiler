package tsystems.tchallenge.codecompiler.api.dto;

import tsystems.tchallenge.codecompiler.utils.IdContainer;

public interface IdAware {
    String getId();

    default IdAware justId() {
        return new IdContainer(getId());
    }
}
