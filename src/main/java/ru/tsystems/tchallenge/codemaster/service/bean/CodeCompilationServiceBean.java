package ru.tsystems.tchallenge.codemaster.service.bean;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import ru.tsystems.tchallenge.codemaster.api.model.CompileInvoice;
import ru.tsystems.tchallenge.codemaster.api.model.CompileResult;
import ru.tsystems.tchallenge.codemaster.domain.models.CodeCompilationStatus;
import ru.tsystems.tchallenge.codemaster.domain.models.CodeLanguage;
import ru.tsystems.tchallenge.codemaster.domain.models.CompileResultEntity;
import ru.tsystems.tchallenge.codemaster.domain.repositories.CodeCompilationResultRepository;
import ru.tsystems.tchallenge.codemaster.reliability.OperationException;
import ru.tsystems.tchallenge.codemaster.reliability.OperationExceptionBuilder;
import ru.tsystems.tchallenge.codemaster.service.CodeCompilationService;
import ru.tsystems.tchallenge.codemaster.service.DockerContainerService;
import ru.tsystems.tchallenge.codemaster.service.ResourceManager;
import ru.tsystems.tchallenge.codemaster.service.converter.CodeCompilationResultConverter;
import ru.tsystems.tchallenge.codemaster.service.model.ContainerExecutionResult;

import java.nio.file.Path;

import static ru.tsystems.tchallenge.codemaster.reliability.OperationExceptionBuilder.internal;
import static ru.tsystems.tchallenge.codemaster.reliability.OperationResultStatus.FAILURE_LOGIC_RECORD_NOT_FOUND;
import static ru.tsystems.tchallenge.codemaster.service.model.ContainerOption.volumeWritable;
import static ru.tsystems.tchallenge.codemaster.service.model.DockerfileType.COMPILATION;

@RequiredArgsConstructor
@Service
@Log4j2
public class CodeCompilationServiceBean implements CodeCompilationService {
    private final CodeCompilationResultRepository codeCompilationResultRepository;
    private final DockerContainerService dockerContainerService;
    private final ResourceManager resourceManager;
    private final CodeCompilationResultConverter codeCompilationResultConverter;

    @Override
    public CompileResult compileFile(CompileInvoice invoice) {
        CodeLanguage codeLanguage = CodeLanguage.valueOf(invoice.getLanguage().name());
        Path workDir = resourceManager.createWorkDir(codeLanguage);
        resourceManager.createAndWriteCodeFile(workDir, invoice.getSourceCode());

        ContainerExecutionResult containerExecutionResult;
        try {
            containerExecutionResult = dockerContainerService
                    .executeContainer(workDir, COMPILATION, volumeWritable());
            log.info("Compilation result: " + containerExecutionResult);
        } catch (Exception e) {
            throw internal(invoice, e);
        }

        CompileResultEntity result = buildResult(codeLanguage, workDir.getFileName().toString(),
                containerExecutionResult);
        codeCompilationResultRepository.save(result);
        log.info(result);
        return codeCompilationResultConverter.toDto(result);
    }

    public CompileResult getCompilationResult(String id) {
        return codeCompilationResultRepository.findById(id)
                .map(codeCompilationResultConverter::toDto)
                .orElseThrow(() -> this.compilationResultNotFound(id));
    }

    private CompileResultEntity buildResult(CodeLanguage language, String workDirName,
                                            ContainerExecutionResult result) {
        CodeCompilationStatus status;
        if (result.getExitCode() != 0) {
            status = CodeCompilationStatus.COMPILATION_ERROR;
        } else {
            status = CodeCompilationStatus.OK;
        }

        return CompileResultEntity.builder()
                .cmpErr(result.getStderr())
                .status(status)
                .language(language)
                .workDirName(workDirName)
                .build();
    }



    private OperationException compilationResultNotFound(String id) {
        return OperationExceptionBuilder.builder()
                .description("Compilation result with specified id not found")
                .type(FAILURE_LOGIC_RECORD_NOT_FOUND)
                .attachment(id)
                .build();
    }

}
