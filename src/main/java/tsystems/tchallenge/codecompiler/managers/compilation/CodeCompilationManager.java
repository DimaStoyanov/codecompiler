package tsystems.tchallenge.codecompiler.managers.compilation;

import tsystems.tchallenge.codecompiler.api.dto.DockerCompilationResult;
import tsystems.tchallenge.codecompiler.domain.repositories.CodeCompilationResultRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.file.Paths;

import static java.lang.ClassLoader.getSystemResource;
import static tsystems.tchallenge.codecompiler.domain.models.CodeLanguage.JAVA;

@Service
public class CodeCompilationManager {


    private final CodeCompilationResultRepository codeCompilationResultRepository;
    private final DockerCompilationManager dockerCompilationManager;

    @Autowired
    public CodeCompilationManager(CodeCompilationResultRepository codeCompilationResultRepository,
                                  DockerCompilationManager dockerCompilationManager) throws Exception{
        this.codeCompilationResultRepository = codeCompilationResultRepository;
        this.dockerCompilationManager = dockerCompilationManager;
        DockerCompilationResult output =
                this.dockerCompilationManager.compileFile(JAVA, Paths.get(getSystemResource("java").toURI()));
        System.out.println(output);
    }




}
