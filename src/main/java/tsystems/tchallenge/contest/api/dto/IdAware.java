package tsystems.tchallenge.contest.api.dto;

import tsystems.tchallenge.contest.utils.IdContainer;

public interface IdAware {
    String getId();

    default IdAware justId() {
        return new IdContainer(getId());
    }
}
