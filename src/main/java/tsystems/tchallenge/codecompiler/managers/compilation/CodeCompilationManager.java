package tsystems.tchallenge.codecompiler.managers.compilation;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tsystems.tchallenge.codecompiler.api.dto.CodeCompilationResultDto;
import tsystems.tchallenge.codecompiler.api.dto.CompileSubmissionInvoice;
import tsystems.tchallenge.codecompiler.api.dto.ContainerExecutionResult;
import tsystems.tchallenge.codecompiler.converters.CodeCompilationResultConverter;
import tsystems.tchallenge.codecompiler.domain.models.CodeCompilationResult;
import tsystems.tchallenge.codecompiler.domain.models.CodeCompilationStatus;
import tsystems.tchallenge.codecompiler.domain.models.CodeLanguage;
import tsystems.tchallenge.codecompiler.domain.repositories.CodeCompilationResultRepository;
import tsystems.tchallenge.codecompiler.managers.docker.DockerContainerManager;
import tsystems.tchallenge.codecompiler.managers.resources.ServiceResourceManager;
import tsystems.tchallenge.codecompiler.reliability.exceptions.OperationException;
import tsystems.tchallenge.codecompiler.reliability.exceptions.OperationExceptionBuilder;

import java.nio.file.Path;

import static tsystems.tchallenge.codecompiler.managers.docker.DockerContainerManager.Option.volumeWritable;
import static tsystems.tchallenge.codecompiler.managers.resources.DockerfileType.COMPILATION;
import static tsystems.tchallenge.codecompiler.reliability.exceptions.OperationExceptionBuilder.internal;
import static tsystems.tchallenge.codecompiler.reliability.exceptions.OperationExceptionType.ERR_COMPILATION_RESULT;

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
