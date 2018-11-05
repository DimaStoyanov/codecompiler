package ru.tsystems.tchallenge.contest.codemaster.utils.data;

import ru.tsystems.tchallenge.contest.codemaster.api.dto.IdAware;

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
