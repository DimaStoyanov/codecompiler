package tsystems.tchallenge.codecompiler.managers.resources;

import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Service;
import tsystems.tchallenge.codecompiler.domain.models.CodeLanguage;
import tsystems.tchallenge.codecompiler.reliability.exceptions.OperationExceptionBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import static java.lang.ClassLoader.getSystemResource;
import static tsystems.tchallenge.codecompiler.reliability.exceptions.OperationExceptionBuilder.internal;
import static tsystems.tchallenge.codecompiler.reliability.exceptions.OperationExceptionType.ERR_INTERNAL;

@Service
public class ResourceManager {

    public static final Charset UTF8 = Charset.forName("UTF-8");
    public final String codeSuffix = "/code";
    public final String languagesDirName = "languages";
    public final String inputFileName = "input.txt";
    public final String outputFileName = "output.txt";

    public Path getDockerfileDir(CodeLanguage language, DockerfileType dockerfileType) {
        Path languageDir = getLanguageDir(language);
        return Paths.get(languageDir.toAbsolutePath() + dockerfileType.suffixPath);
    }

    public Path getLanguageDir(CodeLanguage language) {
        URL systemResource = getSystemResource(languagesDirName + "/" +
                language.toString().toLowerCase());
        try {
            return Paths.get(systemResource.toURI());
        } catch (URISyntaxException e) {
            throw new IllegalStateException("Resource directory not found", e);
        }
    }


    public Path getCodeDir(CodeLanguage language) {
        Path languageDir = getLanguageDir(language);
        return Paths.get(languageDir.toAbsolutePath() + codeSuffix);
    }


    public String getCodeDirPath(CodeLanguage language) {
        return getCodeDir(language)
                .toAbsolutePath()
                .toString();
    }

    public Path getSubmissionDir(CodeLanguage language, String submissionDirName) {
        return Paths.get(getCodeDirPath(language) + "/" + submissionDirName);
    }

    public String getSubmissionDirPath(CodeLanguage language, String submissionDirName) {
        return getSubmissionDir(language, submissionDirName)
                .toAbsolutePath()
                .toString();
    }

    public Path createCodeFile(CodeLanguage language) {
        String submissionDirPath = getSubmissionDirPath(language, UUID.randomUUID().toString());
        Path path = Paths.get(submissionDirPath + "/" + language.sourceFileName());

        try {
            Files.createDirectories(Paths.get(submissionDirPath));
            Files.createFile(path);
        } catch (IOException e) {
            throw internal(e);
        }
        return path;

    }

    public Path createInputFile(String workDirPath) {

        Path inputFile = Paths.get(workDirPath + "/" + inputFileName);
        try {
            if (!Files.exists(inputFile)) {
                Files.createFile(inputFile);
            }
        } catch (IOException e) {
            throw internal(e);
        }
        return inputFile;
    }

    public String getInputPath(String workDirPath) {
        return workDirPath + "/" + inputFileName;
    }

    public String getOutputPath(String workDirPath) {
        return workDirPath + "/" + outputFileName;
    }

    public String readOutputFileIfAvailable(String workDirPath) {
        Path outputFile = Paths.get(getOutputPath(workDirPath));

        if (!Files.isRegularFile(outputFile)) {
            return null;
        }

        return readFile(outputFile);
    }

    public String getCompiledFilePath(CodeLanguage language, Path fileToCompilePath) {
        String baseName = FilenameUtils.getBaseName(fileToCompilePath.toAbsolutePath().toString());
        String workDirPath = fileToCompilePath.getParent().toAbsolutePath().toString();
        String fileName = baseName + "." + language.compiledExt;
        return workDirPath + "/" + fileName;
    }

    public void validateFileExists(String path) {
        if (!Files.isRegularFile(Paths.get(path))) {
            throw OperationExceptionBuilder
                    .builder()
                    .description("File not exist")
                    .type(ERR_INTERNAL)
                    .attachment(path)
                    .build();
        }
    }


    public String getWorkDirPath(String codeFilePath) {
        return Paths.get(codeFilePath).getParent().toAbsolutePath().toString();
    }

    public String getSourceCode(String workDir) {
        Path sourceFile = getSourceFile(workDir);
        return readFile(sourceFile);
    }

    public Path getSourceFile(String workDir) {
        Path codeDir = Paths.get(workDir).getParent();
        CodeLanguage language = CodeLanguage.valueOf(codeDir.getFileName().toString().toUpperCase());
        return Paths.get(workDir + "/" + language.sourceFileName());
    }

    public String readFile(String filePath) {
        return readFile(Paths.get(filePath));
    }


    private String readFile(Path file) {
        try {
            return new String(Files.readAllBytes(file));
        } catch (IOException e) {
            throw internal(e);
        }
    }


}
