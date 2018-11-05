package ru.tsystems.tchallenge.codemaster.service;

import ru.tsystems.tchallenge.codemaster.domain.models.CodeLanguage;
import ru.tsystems.tchallenge.codemaster.service.model.DockerfileType;

public interface DockerImageService {
    String getImageId(CodeLanguage language, DockerfileType type);
}
