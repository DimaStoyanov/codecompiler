package ru.tsystems.tchallenge.contest.codemaster.api.dto;

import ru.tsystems.tchallenge.contest.codemaster.utils.data.IdContainer;

public interface IdAware {
    String getId();

    default IdAware justId() {
        return new IdContainer(getId());
    }
}
