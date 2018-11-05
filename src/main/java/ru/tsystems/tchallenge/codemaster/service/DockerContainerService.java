package ru.tsystems.tchallenge.codemaster.service;

import ru.tsystems.tchallenge.codemaster.service.model.ContainerExecutionResult;
import ru.tsystems.tchallenge.codemaster.service.model.DockerfileType;
import ru.tsystems.tchallenge.codemaster.service.model.ContainerOption;

import java.nio.file.Path;

public interface DockerContainerService {

    ContainerExecutionResult executeContainer(Path workDir, DockerfileType dockerfileType,
                                              ContainerOption... options) throws Exception;
}

