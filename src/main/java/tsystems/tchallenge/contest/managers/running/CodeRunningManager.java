package tsystems.tchallenge.contest.managers.running;

import com.google.common.base.Strings;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import tsystems.tchallenge.contest.api.dto.CodeRunInvoice;
import tsystems.tchallenge.contest.api.dto.CodeRunResultDto;
import tsystems.tchallenge.contest.api.dto.ContainerExecutionResult;
import tsystems.tchallenge.contest.converters.CodeRunResultConverter;
import tsystems.tchallenge.contest.domain.models.CodeCompilationResult;
import tsystems.tchallenge.contest.domain.models.CodeRunResult;
import tsystems.tchallenge.contest.domain.models.CodeRunStatus;
import tsystems.tchallenge.contest.domain.repositories.CodeCompilationResultRepository;
import tsystems.tchallenge.contest.domain.repositories.CodeRunResultRepository;
import tsystems.tchallenge.contest.managers.docker.DockerContainerManager;
import tsystems.tchallenge.contest.managers.resources.ServiceResourceManager;
import tsystems.tchallenge.contest.reliability.exceptions.OperationException;
import tsystems.tchallenge.contest.reliability.exceptions.OperationExceptionBuilder;

import java.nio.file.Path;
import java.time.Duration;

import static java.time.temporal.ChronoUnit.MILLIS;
import static tsystems.tchallenge.contest.managers.docker.DockerContainerManager.Option.*;
import static tsystems.tchallenge.contest.managers.resources.DockerfileType.RUNNING;
import static tsystems.tchallenge.contest.reliability.exceptions.OperationExceptionBuilder.internal;
import static tsystems.tchallenge.contest.reliability.exceptions.OperationExceptionType.ERR_COMPILATION_RESULT;
import static tsystems.tchallenge.contest.reliability.exceptions.OperationExceptionType.ERR_RUN_RESULT;

@Service
@Log4j2
@RequiredArgsConstructor
public class CodeRunningManager {

    private final DockerContainerManager dockerContainerManager;
    private final CodeRunResultRepository codeRunResultRepository;
    private final CodeCompilationResultRepository codeCompilationResultRepository;
    private final CodeRunResultConverter codeRunResultConverter;
    private final ServiceResourceManager resourceManager;

    public CodeRunResultDto runCode(CodeRunInvoice invoice) {
        // Validate and preset invoice
        invoice.validate();
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

    public CodeRunResultDto getRunCodeResult(String id) {
        return codeRunResultRepository.findById(id)
                .map(codeRunResultConverter::toDto)
                .orElseThrow(() -> runResultNotFound(id));

    }

    private void setDefaultsIfMissing(CodeRunInvoice invoice) {
        if (invoice.getExecutionTimeLimit() == null) {
            invoice.setExecutionTimeLimit(5_000L);
        }

        if (invoice.getMemoryLimit() == null) {
            invoice.setMemoryLimit(256 * 1024 * 1024L);
        }
    }

    private CodeCompilationResult findCompilationResult(String id) {
        return codeCompilationResultRepository
                .findById(id)
                .orElseThrow(() -> compilationResultNotFound(id));
    }

    private Path createWorkDir(CodeCompilationResult compilationResult) {
        Path workDir = resourceManager.createWorkDir(compilationResult.getLanguage());
        Path compilationWorkDir = resourceManager
                .getWorkDir(compilationResult.getWorkDirName(), compilationResult.getLanguage());
        resourceManager.cloneWorkDir(compilationWorkDir, workDir);
        return workDir;
    }

    private ContainerExecutionResult executeContainer(Path workDir, CodeRunInvoice invoice) {
        ContainerExecutionResult containerExecutionResult;
        try {
            containerExecutionResult = dockerContainerManager
                    .startContainer(workDir, RUNNING,
                            volumeWritable(),
                            timeLimit(invoice.getExecutionTimeLimit()),
                            memoryLimit(invoice.getMemoryLimit()));
            log.info("Compilation result: " + containerExecutionResult);
        } catch (Exception e) {
            throw internal(invoice, e);
        }
        return containerExecutionResult;
    }

    private CodeRunResult buildResult(CodeCompilationResult compilationResult,
                                      ContainerExecutionResult result,
                                      CodeRunInvoice invoice,
                                      String workDirName) {
        return CodeRunResult.builder()
                .status(status(result, invoice))
                .language(compilationResult.getLanguage())
                .time(result.getExecutionTime().toMillis())
                .memory(result.getMemoryUsage())
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
