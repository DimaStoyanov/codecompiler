package tsystems.tchallenge.contest.utils;

import tsystems.tchallenge.contest.api.dto.IdAware;

public class IdContainer implements IdAware {
    private final String id;

    public IdContainer(String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return id;
    }
}
