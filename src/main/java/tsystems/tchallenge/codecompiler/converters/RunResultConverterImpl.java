package tsystems.tchallenge.codecompiler.converters;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import tsystems.tchallenge.codecompiler.api.dto.CodeRunResultDto;
import tsystems.tchallenge.codecompiler.domain.models.CodeRunResult;
import tsystems.tchallenge.codecompiler.managers.resources.ServiceResourceManager;

import java.nio.file.Path;

@Primary
@Service
@RequiredArgsConstructor
public class RunResultConverterImpl implements CodeRunResultConverter {
    private final CodeRunResultConverter codeRunResultConverter;
    private final ServiceResourceManager resourceManager;

    @Override
    public CodeRunResultDto toDto(CodeRunResult result) {
        CodeRunResultDto dto = codeRunResultConverter.toDto(result);
        Path workDir = resourceManager.getWorkDir(result.getWorkDirName(), dto.getLanguage());
        dto.setInput(resourceManager.readInput(workDir));
        dto.setOutput(resourceManager.readOutput(workDir));
        return dto;
    }
}
