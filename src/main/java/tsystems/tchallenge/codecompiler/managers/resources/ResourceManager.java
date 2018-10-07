package tsystems.tchallenge.codecompiler.managers.resources;

import org.apache.commons.compress.compressors.FileNameUtil;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Service;
import tsystems.tchallenge.codecompiler.domain.models.CodeLanguage;
import tsystems.tchallenge.codecompiler.reliability.exceptions.OperationExceptionBuilder;
import tsystems.tchallenge.codecompiler.reliability.exceptions.OperationExceptionType;

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
    private final String submissionFilePrefix = "submission";

    public Path getDockerfileDir(CodeLanguage language, DockerfileType dockerfileType) {
        Path languageDir = getLanguageDir(language);
        return Paths.get(languageDir.toAbsolutePath() + dockerfileType.suffixPath);
    }

    public Path getLanguageDir(CodeLanguage language) {
        URL systemResource = getSystemResource("languages/" + language.toString().toLowerCase());
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

    public Path createCodeFile(CodeLanguage language) {
        String codeDirPath = getCodeDirPath(language);
        String fileName = submissionFilePrefix + UUID.randomUUID().toString() + "." + language.ext;
        Path path = Paths.get(codeDirPath + "/" + fileName);

        try {
            Files.createFile(path);
        } catch (IOException e) {
            throw internal(e);
        }
        return path;

    }

    public String getCompiledFilePath(CodeLanguage language, Path fileToCompilePath) {
        String baseName = FilenameUtils.getBaseName(fileToCompilePath.toAbsolutePath().toString());
        String filePath = getCodeDirPath(language) + "/" + baseName + "."  + language.compiledExt;
        Path path = Paths.get(filePath);
        return path.toAbsolutePath().toString();
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
}
