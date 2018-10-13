package tsystems.tchallenge.codecompiler.managers.running;

import com.google.common.base.Strings;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tsystems.tchallenge.codecompiler.api.dto.CodeCompilationResultDto;
import tsystems.tchallenge.codecompiler.api.dto.CodeRunInvoice;
import tsystems.tchallenge.codecompiler.api.dto.CodeRunResultDto;
import tsystems.tchallenge.codecompiler.api.dto.ContainerExecutionResult;
import tsystems.tchallenge.codecompiler.converters.CodeRunResultConverter;
import tsystems.tchallenge.codecompiler.domain.models.*;
import tsystems.tchallenge.codecompiler.domain.repositories.CodeCompilationResultRepository;
import tsystems.tchallenge.codecompiler.domain.repositories.CodeRunResultRepository;
import tsystems.tchallenge.codecompiler.managers.docker.DockerContainerManager;
import tsystems.tchallenge.codecompiler.managers.resources.ResourceManager;
import tsystems.tchallenge.codecompiler.reliability.exceptions.OperationException;
import tsystems.tchallenge.codecompiler.reliability.exceptions.OperationExceptionBuilder;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;

import static java.time.temporal.ChronoUnit.MILLIS;
import static tsystems.tchallenge.codecompiler.managers.docker.DockerContainerManager.Option.memoryLimit;
import static tsystems.tchallenge.codecompiler.managers.docker.DockerContainerManager.Option.timeLimit;
import static tsystems.tchallenge.codecompiler.managers.docker.DockerContainerManager.Option.volumeWritable;
import static tsystems.tchallenge.codecompiler.managers.resources.DockerfileType.RUNNING;
import static tsystems.tchallenge.codecompiler.managers.resources.ResourceManager.UTF8;
import static tsystems.tchallenge.codecompiler.reliability.exceptions.OperationExceptionBuilder.internal;
import static tsystems.tchallenge.codecompiler.reliability.exceptions.OperationExceptionType.ERR_COMPILATION_RESULT;
import static tsystems.tchallenge.codecompiler.reliability.exceptions.OperationExceptionType.ERR_RUN_RESULT;

@Service
@Log4j2
public class CodeRunningManager {

    private final DockerContainerManager dockerContainerManager;
    private final CodeRunResultRepository codeRunResultRepository;
    private final CodeCompilationResultRepository codeCompilationResultRepository;
    private final CodeRunResultConverter codeRunResultConverter;
    private final ResourceManager resourceManager;

    @Autowired
    public CodeRunningManager(DockerContainerManager dockerContainerManager,
                              CodeRunResultRepository codeRunResultRepository,
                              CodeCompilationResultRepository codeCompilationResultRepository,
                              CodeRunResultConverter codeRunResultConverter,
                              ResourceManager resourceManager) {
        this.dockerContainerManager = dockerContainerManager;
        this.codeRunResultRepository = codeRunResultRepository;
        this.codeCompilationResultRepository = codeCompilationResultRepository;
        this.codeRunResultConverter = codeRunResultConverter;
        this.resourceManager = resourceManager;
    }

    public CodeRunResultDto runCode(CodeRunInvoice invoice) {
        invoice.validate();
        setDefaultsIfMissing(invoice);


        CodeCompilationResult compilationResult = codeCompilationResultRepository
                .findById(invoice.getSubmissionId())
                .orElseThrow(() -> compilationResultNotFound(invoice.getSubmissionId()));

        if (!Strings.isNullOrEmpty(invoice.getInput())) {
            attachInput(compilationResult.getCompiledFilePath(), invoice.getInput());
        }
        Path workDir = Paths.get(compilationResult.getCompiledFilePath()).getParent();

        ContainerExecutionResult containerExecutionResult;
        try {
            containerExecutionResult = dockerContainerManager
                    .startContainer(workDir, RUNNING, volumeWritable(),
                            timeLimit(invoice.getExecutionTimeLimit()),
                            memoryLimit(invoice.getMemoryLimit()));
            log.info("Compilation result: " + containerExecutionResult);
        } catch (Exception e) {
            throw internal(invoice, e);
        }

        CodeRunResult result = buildResult(compilationResult, containerExecutionResult, invoice);
        codeRunResultRepository.save(result);
        log.info(result);
        return codeRunResultConverter.toDto(result);
    }

    public CodeRunResultDto getRunCodeResult(String id) {
        return codeRunResultRepository.findById(id)
        .map(codeRunResultConverter::toDto)
                .orElseThrow(() -> runResultNotFound(id));

    }

    private CodeRunResult buildResult(CodeCompilationResult compilationResult,
                                              ContainerExecutionResult result,
                                              CodeRunInvoice invoice) {
        CodeRunStatus status = status(result, invoice);
        String workDirPath = resourceManager.getWorkDirPath(compilationResult.getCompiledFilePath());
        String output = resourceManager.readOutputFileIfAvailable(workDirPath);
        output = output == null ? result.getStdout() : output;
        String inputPath = resourceManager.getInputPath(workDirPath);

        return CodeRunResult.builder()
                .status(status)
                .language(compilationResult.getLanguage())
                .output(output)
                .outputPath(resourceManager.getOutputPath(workDirPath))
                .input(invoice.getInput())
                .time(result.getExecutionTime().toMillis())
                .inputPath(inputPath)
                .memory(result.getMemoryUsage())
                .stderr(result.getStderr())
                .languageName(compilationResult.getLanguage().name)
                .build();
    }

    private CodeRunStatus status(ContainerExecutionResult result, CodeRunInvoice invoice) {
        Long exitCode = result.getExitCode();
        if (result.getOomKilled()) {
            return CodeRunStatus.MEMORY_LIMIT;
        }
        if (exitCode != 0) {
            Duration timeLimit = Duration.of(invoice.getExecutionTimeLimit(), MILLIS);
            if (result.getExecutionTime().compareTo(timeLimit) >= 0) {
                return CodeRunStatus.TIME_LIMIT;
            }
            return CodeRunStatus.RUNTIME_ERROR;
        } else {
            return CodeRunStatus.OK;
        }
    }


    private void attachInput(String compiledFilePath, String input) {
        String workDirPath = resourceManager.getWorkDirPath(compiledFilePath);
        Path inputFile = resourceManager.createInputFile(workDirPath);

        try (BufferedWriter writer = Files.newBufferedWriter(inputFile, UTF8)) {
            writer.write(input);
        } catch (IOException e) {
            throw internal(e);
        }
    }


    private void setDefaultsIfMissing(CodeRunInvoice invoice) {
        if (invoice.getExecutionTimeLimit() == null) {
            invoice.setExecutionTimeLimit(5_000L);
        }

        if (invoice.getMemoryLimit() == null) {
            invoice.setMemoryLimit(256 * 1024 * 1024L);
        }
    }




    private OperationException compilationResultNotFound(String id) {
        return OperationExceptionBuilder.builder()
                .description("Compilation result with specified id not found")
                .type(ERR_COMPILATION_RESULT)
                .attachment(id)
                .build();
    }



    private OperationException runResultNotFound(String id) {
        return OperationExceptionBuilder.builder()
                .description("Run result with specified id not found")
                .type(ERR_RUN_RESULT)
                .attachment(id)
                .build();
    }
}
