package ru.tsystems.tchallenge.codemaster.service.bean;

import com.google.common.base.Strings;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import ru.tsystems.tchallenge.codemaster.api.model.RunInvoice;
import ru.tsystems.tchallenge.codemaster.api.model.RunResult;
import ru.tsystems.tchallenge.codemaster.domain.models.CodeRunStatus;
import ru.tsystems.tchallenge.codemaster.domain.models.CompileResultEntity;
import ru.tsystems.tchallenge.codemaster.domain.models.RunResultEntity;
import ru.tsystems.tchallenge.codemaster.domain.repositories.CodeCompilationResultRepository;
import ru.tsystems.tchallenge.codemaster.domain.repositories.CodeRunResultRepository;
import ru.tsystems.tchallenge.codemaster.reliability.OperationException;
import ru.tsystems.tchallenge.codemaster.reliability.OperationExceptionBuilder;
import ru.tsystems.tchallenge.codemaster.service.CodeRunService;
import ru.tsystems.tchallenge.codemaster.service.DockerContainerService;
import ru.tsystems.tchallenge.codemaster.service.ResourceManager;
import ru.tsystems.tchallenge.codemaster.service.converter.CodeRunResultConverter;
import ru.tsystems.tchallenge.codemaster.service.model.ContainerExecutionResult;

import java.nio.file.Path;
import java.time.Duration;

import static java.time.temporal.ChronoUnit.MILLIS;
import static ru.tsystems.tchallenge.codemaster.reliability.OperationExceptionBuilder.internal;
import static ru.tsystems.tchallenge.codemaster.reliability.OperationResultStatus.FAILURE_LOGIC_RECORD_NOT_FOUND;
import static ru.tsystems.tchallenge.codemaster.service.model.ContainerOption.*;
import static ru.tsystems.tchallenge.codemaster.service.model.DockerfileType.RUNNING;

@Service
@RequiredArgsConstructor
@Log4j2
public class CodeRunServiceBean implements CodeRunService {
    private final DockerContainerService dockerContainerService;
    private final CodeRunResultRepository codeRunResultRepository;
    private final CodeCompilationResultRepository codeCompilationResultRepository;
    private final CodeRunResultConverter codeRunResultConverter;
    private final ResourceManager resourceManager;

    @Override
    public RunResult runCode(RunInvoice invoice) {
        setDefaultsIfMissing(invoice);

        // Prepare workspace
        var compilationResult = findCompilationResult(invoice.getSubmissionId());
        Path workDir = createWorkDir(compilationResult);
        if (!Strings.isNullOrEmpty(invoice.getInput())) {
            resourceManager.createAndWriteInputFile(workDir, invoice.getInput());
        }

        // Execute
        ContainerExecutionResult containerExecutionResult = executeContainer(workDir, invoice);


        // Collect results
        writeStdoutToFileIfMissing(workDir, containerExecutionResult.getStdout());
        var result = buildResult(compilationResult, containerExecutionResult, invoice,
                workDir.getFileName().toString());
        codeRunResultRepository.save(result);
        log.info(result);
        return codeRunResultConverter.toDto(result);
    }

    @Override
    public RunResult getResult(String id) {
        return codeRunResultRepository.findById(id)
                .map(codeRunResultConverter::toDto)
                .orElseThrow(() -> runResultNotFound(id));

    }

    private void setDefaultsIfMissing(RunInvoice invoice) {
        if (invoice.getTimeLimit() == null) {
            invoice.setTimeLimit(5_000);
        }

        if (invoice.getMemoryLimit() == null) {
            invoice.setMemoryLimit(256 * 1024 * 1024);
        }
    }

    private CompileResultEntity findCompilationResult(String id) {
        return codeCompilationResultRepository
                .findById(id)
                .orElseThrow(() -> compilationResultNotFound(id));
    }

    private Path createWorkDir(CompileResultEntity compilationResult) {
        Path workDir = resourceManager.createWorkDir(compilationResult.getLanguage());
        Path compilationWorkDir = resourceManager
                .getWorkDir(compilationResult.getWorkDirName(), compilationResult.getLanguage());
        resourceManager.cloneWorkDir(compilationWorkDir, workDir);
        return workDir;
    }

    private ContainerExecutionResult executeContainer(Path workDir, RunInvoice invoice) {
        ContainerExecutionResult containerExecutionResult;
        try {
            containerExecutionResult = dockerContainerService
                    .executeContainer(workDir, RUNNING,
                            volumeWritable(),
                            timeLimit(invoice.getTimeLimit()),
                            memoryLimit(invoice.getMemoryLimit()));
            log.info("Compilation result: " + containerExecutionResult);
        } catch (Exception e) {
            throw internal(invoice, e);
        }
        return containerExecutionResult;
    }

    private RunResultEntity buildResult(CompileResultEntity compilationResult,
                                        ContainerExecutionResult result,
                                        RunInvoice invoice,
                                        String workDirName) {
        return RunResultEntity.builder()
                .status(status(result, invoice))
                .language(compilationResult.getLanguage())
                .time(result.getExecutionTime().toMillis())
                .stderr(result.getStderr())
                .workDirName(workDirName)
                .compileSubmissionId(compilationResult.getId())
                .languageName(compilationResult.getLanguage().name)
                .build();
    }

    private void writeStdoutToFileIfMissing(Path workDir, String stdout) {
        String outputFile = resourceManager.readOutput(workDir);
        if (outputFile == null) {
            resourceManager.createAndWriteOutputFile(workDir, stdout);
        }
    }

    private CodeRunStatus status(ContainerExecutionResult result, RunInvoice invoice) {
        Long exitCode = result.getExitCode();
        if (result.getOomKilled()) {
            return CodeRunStatus.MEMORY_LIMIT;
        }
        if (exitCode != 0) {
            Duration timeLimit = Duration.of(invoice.getTimeLimit(), MILLIS);
            if (result.getExecutionTime().compareTo(timeLimit) >= 0) {
                return CodeRunStatus.TIME_LIMIT;
            }
            return CodeRunStatus.RUNTIME_ERROR;
        } else {
            return CodeRunStatus.OK;
        }
    }

    private OperationException compilationResultNotFound(String id) {
        return OperationExceptionBuilder.builder()
                .description("Compilation result with specified id not found")
                .type(FAILURE_LOGIC_RECORD_NOT_FOUND)
                .attachment(id)
                .build();
    }


    private OperationException runResultNotFound(String id) {
        return OperationExceptionBuilder.builder()
                .description("Run result with specified id not found")
                .type(FAILURE_LOGIC_RECORD_NOT_FOUND)
                .attachment(id)
                .build();
    }
}
