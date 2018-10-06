package tsystems.tchallenge.codecompiler.managers.compilation;

import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.DockerClient.BuildParam;
import com.spotify.docker.client.DockerClient.LogsParam;
import com.spotify.docker.client.LogStream;
import com.spotify.docker.client.exceptions.DockerException;
import com.spotify.docker.client.messages.ContainerConfig;
import com.spotify.docker.client.messages.ContainerCreation;
import com.spotify.docker.client.messages.HostConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tsystems.tchallenge.codecompiler.api.dto.DockerCompilationResult;
import tsystems.tchallenge.codecompiler.domain.models.CodeLanguage;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.file.Path;
import java.nio.file.Paths;

import static com.spotify.docker.client.DockerClient.BuildParam.create;
import static com.spotify.docker.client.DockerClient.LogsParam.stderr;
import static com.spotify.docker.client.DockerClient.LogsParam.stdout;
import static com.spotify.docker.client.messages.HostConfig.Bind.from;
import static java.lang.ClassLoader.getSystemResource;
import static java.net.URLEncoder.encode;

@Service
public class DockerCompilationManager {


    private final String codeSuffix = "/code";
    private final String compileSuffix = "/compilation";

    private final DockerClient docker;


    @Autowired
    public DockerCompilationManager(DockerClient docker) {
        this.docker = docker;
    }


    public DockerCompilationResult compileFile(CodeLanguage language, Path file) throws Exception {
        Path languageDir = getLanguageDir(language);
        HostConfig hostConfig = getHostConfig(languageDir);

        final ContainerConfig containerConfig = ContainerConfig.builder()
                .hostConfig(hostConfig)
                .image(createImage(language, file))
                .build();

        ContainerCreation creation = docker.createContainer(containerConfig);
        String id = creation.id();

        docker.startContainer(id);
        docker.waitContainer(id);

        return buildResult(id);
    }

    private DockerCompilationResult buildResult(String id) throws Exception {

        String stdout = collectLogs(id, stdout());
        String stderr = collectLogs(id, stderr());
        Long exitCode = docker.inspectContainer(id).state().exitCode();

        return DockerCompilationResult.builder()
                .stdout(stdout)
                .stderr(stderr)
                .exitCode(exitCode)
                .build();

    }

    private String collectLogs(String id, LogsParam... params) throws Exception {
        try (LogStream stream = docker.logs(id, params)) {
            return stream.readFully();
        }

    }

    private String createImage(CodeLanguage language, Path path) {
        Path dockerFileDir = getDockerfileDir(language);
        String fileName = path.getFileName().toString();
        try {
            String buildArgs = "{\"file_name\":\"" + fileName + "\"}";
            BuildParam buildParam = create("buildargs", encode(buildArgs, "UTF-8"));
            return docker.build(dockerFileDir ,buildParam);

        } catch (DockerException | InterruptedException | IOException e) {
            throw new IllegalStateException("Can not build docker image");
        }
    }




    private HostConfig getHostConfig(Path langDir) {
        return HostConfig.builder()
                .appendBinds(from(langDir.toAbsolutePath() + codeSuffix)
                        .to(codeSuffix)
                        .readOnly(false)
                        .build())
                .build();
    }


    private Path getDockerfileDir(CodeLanguage language) {
        Path languageDir = getLanguageDir(language);
        return Paths.get(languageDir.toAbsolutePath() + compileSuffix);
    }

    private Path getLanguageDir(CodeLanguage language) {
        URL systemResource = getSystemResource("languages/" + language.toString().toLowerCase());
        try {
            return Paths.get(systemResource.toURI());
        } catch (URISyntaxException e) {
            throw new IllegalStateException("Resource directory not found", e);
        }
    }

}
