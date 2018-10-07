package tsystems.tchallenge.codecompiler.managers.compilation;

import com.google.common.base.Strings;
import lombok.extern.log4j.Log4j2;
import tsystems.tchallenge.codecompiler.api.dto.CodeSubmissionInvoice;
import tsystems.tchallenge.codecompiler.api.dto.DockerCompilationResult;
import tsystems.tchallenge.codecompiler.domain.models.CodeCompilationResult;
import tsystems.tchallenge.codecompiler.domain.models.CodeCompilationStatus;
import tsystems.tchallenge.codecompiler.domain.models.CodeLanguage;
import tsystems.tchallenge.codecompiler.domain.repositories.CodeCompilationResultRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tsystems.tchallenge.codecompiler.managers.resources.ResourceManager;
import tsystems.tchallenge.codecompiler.reliability.exceptions.OperationException;
import tsystems.tchallenge.codecompiler.reliability.exceptions.OperationExceptionBuilder;
import tsystems.tchallenge.codecompiler.reliability.exceptions.OperationExceptionType;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static java.lang.ClassLoader.getSystemResource;
import static tsystems.tchallenge.codecompiler.domain.models.CodeLanguage.JAVA;
import static tsystems.tchallenge.codecompiler.managers.resources.ResourceManager.UTF8;
import static tsystems.tchallenge.codecompiler.reliability.exceptions.OperationExceptionBuilder.internal;
import static tsystems.tchallenge.codecompiler.reliability.exceptions.OperationExceptionType.ERR_COMPILATION_RESULT;

@Service
@Log4j2
public class CodeCompilationManager {


    private final CodeCompilationResultRepository codeCompilationResultRepository;
    private final DockerCompilationManager dockerCompilationManager;
    private final ResourceManager resourceManager;

    @Autowired
    public CodeCompilationManager(CodeCompilationResultRepository codeCompilationResultRepository,
                                  DockerCompilationManager dockerCompilationManager, ResourceManager resourceManager) throws Exception {
        this.codeCompilationResultRepository = codeCompilationResultRepository;
        this.dockerCompilationManager = dockerCompilationManager;
        this.resourceManager = resourceManager;
        Path java = Paths.get(getSystemResource("languages/java/code/Main.java").toURI());
        DockerCompilationResult output =
                this.dockerCompilationManager.compileFile(JAVA, java);
        System.out.println(output);
    }


    public CodeCompilationResult compileFile(CodeSubmissionInvoice invoice) {
        invoice.validate();
        Path path = copyToFile(invoice);

        DockerCompilationResult dockerCompilationResult;
        try {
            dockerCompilationResult = dockerCompilationManager.compileFile(invoice.getLanguage(), path);
            log.info("Compilation result: " + dockerCompilationResult);
        } catch (Exception e) {
            throw internal(invoice, e);
        }

        CodeCompilationResult result = buildResult(invoice.getLanguage(), path, dockerCompilationResult);
        codeCompilationResultRepository.save(result);
        return result;
    }

    public CodeCompilationResult getCompilationResult(String id) {
        return codeCompilationResultRepository.findById(id)
                .orElseThrow(() -> this.compilationResultNotFound(id));
    }

    private CodeCompilationResult buildResult(CodeLanguage language, Path fileToCompile,
                                              DockerCompilationResult result) {
        CodeCompilationStatus status;

        if (!Strings.isNullOrEmpty(result.getStderr())) {
            status = CodeCompilationStatus.COMPILATION_ERROR;
        } else {
            status = CodeCompilationStatus.OK;
        }

        String compiledFilePath = resourceManager.getCompiledFilePath(language, fileToCompile);
        resourceManager.validateFileExists(compiledFilePath);

        return CodeCompilationResult.builder()
                .cmpErr(result.getStderr())
                .status(status)
                .language(language)
                .compiledFilePath(compiledFilePath)
                .build();
    }

    private Path copyToFile(CodeSubmissionInvoice invoice) {
        Path codeFile = resourceManager.createCodeFile(invoice.getLanguage());
        try (BufferedWriter writer = Files.newBufferedWriter(codeFile, UTF8)) {
            writer.write(invoice.getSourceCode());
        } catch (IOException e) {
            throw internal(e);
        }
        return codeFile;
    }


    private OperationException compilationResultNotFound(String id) {
        return OperationExceptionBuilder.builder()
                .description("Compilation result with specified id not found")
                .type(ERR_COMPILATION_RESULT)
                .attachment(id)
                .build();
    }



}
