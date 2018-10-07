package tsystems.tchallenge.codecompiler.managers.compilation;

import com.spotify.docker.client.DockerClient;
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
import tsystems.tchallenge.codecompiler.managers.resources.ResourceManager;

import java.io.IOException;
import java.nio.file.Path;

import static com.spotify.docker.client.DockerClient.LogsParam.stderr;
import static com.spotify.docker.client.DockerClient.LogsParam.stdout;
import static com.spotify.docker.client.messages.HostConfig.Bind.from;
import static tsystems.tchallenge.codecompiler.managers.resources.DockerfileType.COMPILATION;

@Service
public class DockerCompilationManager {



    private final DockerClient docker;
    private final ResourceManager resourceManager;

    @Autowired
    public DockerCompilationManager(DockerClient docker, ResourceManager resourceManager) {
        this.docker = docker;
        this.resourceManager = resourceManager;
    }


    public DockerCompilationResult compileFile(CodeLanguage language, Path fileToCompile) throws Exception {
        HostConfig hostConfig = getHostConfig(language, fileToCompile);

        final ContainerConfig containerConfig = ContainerConfig.builder()
                .hostConfig(hostConfig)
                .image(createImage(language))
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

    private String createImage(CodeLanguage language) {
        Path dockerFileDir = resourceManager.getDockerfileDir(language, COMPILATION);
        try {
            return docker.build(dockerFileDir);

        } catch (DockerException | InterruptedException | IOException e) {
            throw new IllegalStateException("Can not build docker image");
        }
    }


    // Create bind between container file system and host file system (see docker volumes)
    private HostConfig getHostConfig(CodeLanguage language, Path file) {
        String submissionDir = file.getParent().getFileName().toString();
        return HostConfig.builder()
                .appendBinds(from(resourceManager.getSubmissionDirPath(language, submissionDir))
                        .to(resourceManager.codeSuffix)
                        .readOnly(false)
                        .build())
                .build();
    }

}