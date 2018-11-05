package ru.tsystems.tchallenge.contest.codemaster.managers.compilation;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.tsystems.tchallenge.contest.codemaster.api.dto.CodeCompilationResultDto;
import ru.tsystems.tchallenge.contest.codemaster.api.dto.CompileSubmissionInvoice;
import ru.tsystems.tchallenge.contest.codemaster.api.dto.ContainerExecutionResult;
import ru.tsystems.tchallenge.contest.codemaster.converters.CodeCompilationResultConverter;
import ru.tsystems.tchallenge.contest.codemaster.domain.models.CodeCompilationResult;
import ru.tsystems.tchallenge.contest.codemaster.domain.models.CodeCompilationStatus;
import ru.tsystems.tchallenge.contest.codemaster.domain.models.CodeLanguage;
import ru.tsystems.tchallenge.contest.codemaster.domain.repositories.CodeCompilationResultRepository;
import ru.tsystems.tchallenge.contest.codemaster.managers.docker.DockerContainerManager;
import ru.tsystems.tchallenge.contest.codemaster.managers.resources.ServiceResourceManager;
import ru.tsystems.tchallenge.contest.codemaster.reliability.OperationException;
import ru.tsystems.tchallenge.contest.codemaster.reliability.OperationExceptionBuilder;

import java.nio.file.Path;

import static ru.tsystems.tchallenge.contest.codemaster.managers.docker.DockerContainerManager.Option.volumeWritable;
import static ru.tsystems.tchallenge.contest.codemaster.managers.resources.DockerfileType.COMPILATION;
import static ru.tsystems.tchallenge.contest.codemaster.reliability.OperationExceptionBuilder.internal;
import static ru.tsystems.tchallenge.contest.codemaster.reliability.OperationExceptionType.ERR_COMPILATION_RESULT;

@Service
@Log4j2
public class CodeCompilationManager {


    private final CodeCompilationResultRepository codeCompilationResultRepository;
    private final DockerContainerManager dockerContainerManager;
    private final ServiceResourceManager resourceManager;
    private final CodeCompilationResultConverter codeCompilationResultConverter;

    @Autowired
    public CodeCompilationManager(CodeCompilationResultRepository codeCompilationResultRepository,
                                  DockerContainerManager dockerContainerManager,
                                  ServiceResourceManager resourceManager,
                                  CodeCompilationResultConverter codeCompilationResultConverter) {
        this.codeCompilationResultRepository = codeCompilationResultRepository;
        this.dockerContainerManager = dockerContainerManager;
        this.resourceManager = resourceManager;
        this.codeCompilationResultConverter = codeCompilationResultConverter;
    }


    public CodeCompilationResultDto compileFile(CompileSubmissionInvoice invoice) {
        invoice.validate();
        Path workDir = resourceManager.createWorkDir(invoice.getLanguage());
        resourceManager.createAndWriteCodeFile(workDir, invoice.getSourceCode());

        ContainerExecutionResult containerExecutionResult;
        try {
            containerExecutionResult = dockerContainerManager
                    .startContainer(workDir, COMPILATION, volumeWritable());
            log.info("Compilation result: " + containerExecutionResult);
        } catch (Exception e) {
            throw internal(invoice, e);
        }

        CodeCompilationResult result = buildResult(invoice.getLanguage(), workDir.getFileName().toString(),
                containerExecutionResult);
        codeCompilationResultRepository.save(result);
        log.info(result);
        return codeCompilationResultConverter.toDto(result);
    }

    public CodeCompilationResultDto getCompilationResult(String id) {
        return codeCompilationResultRepository.findById(id)
                .map(codeCompilationResultConverter::toDto)
                .orElseThrow(() -> this.compilationResultNotFound(id));
    }

    private CodeCompilationResult buildResult(CodeLanguage language, String workDirName,
                                              ContainerExecutionResult result) {
        CodeCompilationStatus status;
        if (result.getExitCode() != 0) {
            status = CodeCompilationStatus.COMPILATION_ERROR;
        } else {
            status = CodeCompilationStatus.OK;
        }

        return CodeCompilationResult.builder()
                .cmpErr(result.getStderr())
                .status(status)
                .language(language)
                .workDirName(workDirName)
                .build();
    }



    private OperationException compilationResultNotFound(String id) {
        return OperationExceptionBuilder.builder()
                .description("Compilation result with specified id not found")
                .type(ERR_COMPILATION_RESULT)
                .attachment(id)
                .build();
    }


}
