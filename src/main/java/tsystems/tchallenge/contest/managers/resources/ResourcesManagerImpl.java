package tsystems.tchallenge.contest.managers.resources;

import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;
import tsystems.tchallenge.contest.domain.models.CodeLanguage;

import java.io.BufferedWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import static java.lang.ClassLoader.getSystemResource;
import static tsystems.tchallenge.contest.reliability.exceptions.OperationExceptionBuilder.internal;

@Service
public class ResourcesManagerImpl implements ServiceResourceManager {

    private static final String CODE_SUFFIX = "/code";
    private static final String LANGUAGES_DIR_NAME = "languages";
    private static final String INPUT_FILE_NAME = "input.txt";
    private static final String OUTPUT_FILE_NAME = "output.txt";

    public Path getDockerfileDir(CodeLanguage language, DockerfileType dockerfileType) {
        Path languageDir = getLanguageDir(language);
        return Paths.get(languageDir.toAbsolutePath() + dockerfileType.suffixPath);
    }


    public Path createWorkDir(CodeLanguage language) {
        Path workDir = getWorkDir(language, UUID.randomUUID().toString());
        try {
            Files.createDirectories(workDir);
        } catch (IOException e) {
            throw internal(e);
        }
        return workDir;
    }


    public Path createAndWriteCodeFile(Path workDir, String sourceCode) {
        CodeLanguage codeLanguage = languageByWorkDir(workDir);
        Path path = Paths.get(workDir.toAbsolutePath().toString() + "/" + codeLanguage.sourceFileName());
        writeToFile(path, sourceCode);
        return path;

    }

    public Path createAndWriteInputFile(Path workDir, String input) {
        String workDirPath = workDir.toAbsolutePath().toString();
        Path inputFile = Paths.get(workDirPath + "/" + INPUT_FILE_NAME);
        writeToFile(inputFile, input);
        return inputFile;
    }

    @Override
    public Path createAndWriteOutputFile(Path workDir, String output) {
        String workDirPath = workDir.toAbsolutePath().toString();
        Path outputFile = Paths.get(workDirPath + "/" + OUTPUT_FILE_NAME);
        writeToFile(outputFile, output);
        return outputFile;
    }


    public String readInput(Path workDir) {
        Path input = Paths.get(workDir.toAbsolutePath() + "/" + INPUT_FILE_NAME);
        return readFile(input);
    }

    public String readOutput(Path workDir) {
        Path output = Paths.get(workDir.toAbsolutePath() + "/" + OUTPUT_FILE_NAME);
        return readFile(output);
    }


    public Path getWorkDir(String workDirName, CodeLanguage language) {
        String codeDir = getCodeDirPath(language);
        return Paths.get(codeDir + "/" + workDirName);
    }

    public String readSourceCode(Path workDir) {
        Path sourceFile = getSourceFile(workDir);
        return readFile(sourceFile);
    }

    @Override
    public void cloneWorkDir(Path workDirFrom, Path workDirTo) {
        try {
            FileUtils.copyDirectory(workDirFrom.toFile(), workDirTo.toFile());
        } catch (IOException e) {
            throw internal(e);
        }
    }

    private Path getLanguageDir(CodeLanguage language) {
        URL systemResource = getSystemResource(LANGUAGES_DIR_NAME + "/" +
                language.toString().toLowerCase());
        try {
            return Paths.get(systemResource.toURI());
        } catch (URISyntaxException e) {
            throw new IllegalStateException("Resource directory not found", e);
        }
    }

    private Path getWorkDir(CodeLanguage language, String workDirName) {
        return Paths.get(getCodeDirPath(language) + "/" + workDirName);
    }

    private String getCodeDirPath(CodeLanguage language) {
        Path languageDir = getLanguageDir(language).toAbsolutePath();
        return Paths.get(languageDir + CODE_SUFFIX)
                .toAbsolutePath()
                .toString();
    }

    private Path getSourceFile(Path workDir) {
        Path codeDir = workDir.getParent();
        CodeLanguage language = CodeLanguage.valueOf(codeDir.getFileName().toString().toUpperCase());
        return Paths.get(workDir + "/" + language.sourceFileName());
    }



    private String readFile(Path file) {
        if (!Files.isRegularFile(file)) {
            return null;
        }
        try {
            return new String(Files.readAllBytes(file));
        } catch (IOException e) {
            throw internal(e);
        }
    }

    private void writeToFile(Path file, String content) {
        try (BufferedWriter writer = Files.newBufferedWriter(file, Charset.forName("UTF-8"))) {
            writer.write(content);
        } catch (IOException e) {
            throw internal(e);
        }
    }

    private CodeLanguage languageByWorkDir(Path workDir) {
        Path codeDir = workDir.getParent();
        Path langDir = codeDir.getParent();
        return CodeLanguage.valueOf(langDir.getFileName().toString().toUpperCase());
    }

}
