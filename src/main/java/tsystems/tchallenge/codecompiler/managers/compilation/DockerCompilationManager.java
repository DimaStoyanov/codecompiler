package tsystems.tchallenge.codecompiler.managers.compilation;

import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.DockerClient.LogsParam;
import com.spotify.docker.client.LogStream;
import com.spotify.docker.client.exceptions.DockerException;
import com.spotify.docker.client.messages.ContainerConfig;
import com.spotify.docker.client.messages.ContainerCreation;
import com.spotify.docker.client.messages.HostConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import tsystems.tchallenge.codecompiler.api.dto.DockerCompilationResult;
import tsystems.tchallenge.codecompiler.api.dto.IdAware;
import tsystems.tchallenge.codecompiler.domain.models.CodeCompilationResult;
import tsystems.tchallenge.codecompiler.domain.models.CodeLanguage;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import static com.spotify.docker.client.DockerClient.LogsParam.stderr;
import static com.spotify.docker.client.DockerClient.LogsParam.stdout;
import static com.spotify.docker.client.messages.HostConfig.Bind.from;
import static java.lang.ClassLoader.getSystemResource;

@Service
public class DockerCompilationManager {


    private final String codeSuffix = "/code";
    private final DockerClient docker;
    private Map<CodeLanguage, String> imageByLanguage;


    @Autowired
    public DockerCompilationManager(DockerClient docker) {
        this.docker = docker;
    }

    @PostConstruct
    public void init() {

        imageByLanguage = Arrays.stream(CodeLanguage.values())
                .parallel()
                .map(c -> Pair.of(c, getSystemResource(c.toString().toLowerCase())))
                .map(this::toPath)
                .map(this::buildImage)
                .collect(Collectors.toMap(Pair::getFirst, Pair::getSecond));

    }


    public DockerCompilationResult compileFile(CodeLanguage language, Path directory) throws Exception {
        final ContainerConfig containerConfig = ContainerConfig.builder()
                .hostConfig(getHostConfig(directory))
                .image(imageByLanguage.get(language))
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

    private Pair<CodeLanguage, Path> toPath(Pair<CodeLanguage, URL> pair) {
        try {
            return Pair.of(pair.getFirst(), Paths.get(pair.getSecond().toURI()));
        } catch (URISyntaxException e) {
            throw new IllegalStateException("Resource directory not found", e);
        }
    }

    private Pair<CodeLanguage, String> buildImage(Pair<CodeLanguage, Path> pair) {
        try {
            return Pair.of(pair.getFirst(), docker.build(pair.getSecond()));
        } catch (DockerException | InterruptedException | IOException e) {
            throw new IllegalStateException("Can not build docker image");
        }
    }


    private HostConfig getHostConfig(Path path) {
        return HostConfig.builder()
                .appendBinds(from(path.toAbsolutePath() + codeSuffix)
                        .to(codeSuffix)
                        .readOnly(false)
                        .build())
                .build();
    }

}
