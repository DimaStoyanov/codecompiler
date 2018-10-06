package tsystems.tchallenge.codecompiler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import tsystems.tchallenge.codecompiler.domain.models.CodeLanguage;
import tsystems.tchallenge.codecompiler.managers.compilation.DockerCompilationManager;

import java.nio.file.Paths;

import static java.lang.ClassLoader.getSystemResource;
import static tsystems.tchallenge.codecompiler.domain.models.CodeLanguage.JAVA;

@SpringBootApplication
public class CodeCompilerApplication {

    @Autowired
    static DockerCompilationManager dockerCompilationManager;

    public static void main(String[] args) throws Exception {
        SpringApplication.run(CodeCompilerApplication.class, args);

    }
}
