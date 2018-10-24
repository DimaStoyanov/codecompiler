package tsystems.tchallenge.codecompiler.converters;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import tsystems.tchallenge.codecompiler.api.dto.CodeRunResultDto;
import tsystems.tchallenge.codecompiler.domain.models.CodeRunResult;
import tsystems.tchallenge.codecompiler.domain.repositories.CodeCompilationResultRepository;
import tsystems.tchallenge.codecompiler.managers.resources.ServiceResourceManager;
import tsystems.tchallenge.codecompiler.reliability.exceptions.OperationExceptionBuilder;

import java.nio.file.Path;

@Primary
@Service
@RequiredArgsConstructor
public class RunResultConverterImpl implements CodeRunResultConverter {
    private final CodeRunResultConverter codeRunResultConverter;
    private final ServiceResourceManager resourceManager;
    private final CodeCompilationResultRepository compilationResultRepository;

    @Override
    public CodeRunResultDto toDto(CodeRunResult result) {
        CodeRunResultDto dto = codeRunResultConverter.toDto(result);
        Path workDir = compilationResultRepository.findById(result.getCompileSubmissionId())
                .map(w -> resourceManager.getWorkDir(w.getWorkDirName(), w.getLanguage()))
                .orElseThrow(OperationExceptionBuilder::internal);
        dto.setInput(resourceManager.readInput(workDir));
        dto.setOutput(resourceManager.readOutput(workDir));
        return dto;
    }
}
