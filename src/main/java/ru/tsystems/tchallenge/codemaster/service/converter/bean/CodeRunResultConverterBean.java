package ru.tsystems.tchallenge.codemaster.service.converter.bean;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import ru.tsystems.tchallenge.codemaster.api.model.RunResult;
import ru.tsystems.tchallenge.codemaster.domain.models.RunResultEntity;
import ru.tsystems.tchallenge.codemaster.service.ResourceManager;
import ru.tsystems.tchallenge.codemaster.service.converter.CodeRunResultConverter;

import java.nio.file.Path;

@Primary
@Service
@RequiredArgsConstructor
public class CodeRunResultConverterBean implements CodeRunResultConverter {
    private final CodeRunResultConverter codeRunResultConverter;
    private final ResourceManager resourceManager;

    @Override
    public RunResult toDto(RunResultEntity result) {
        RunResult dto = codeRunResultConverter.toDto(result);
        Path workDir = resourceManager.getWorkDir(result.getWorkDirName(), result.getLanguage());
        dto.setInput(resourceManager.readInput(workDir));
        dto.setOutput(resourceManager.readOutput(workDir));
        return dto;
    }
}
