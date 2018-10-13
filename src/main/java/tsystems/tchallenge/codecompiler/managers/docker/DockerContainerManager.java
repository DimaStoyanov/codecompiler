package tsystems.tchallenge.codecompiler.managers.docker;

import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.LogStream;
import com.spotify.docker.client.messages.*;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;
import tsystems.tchallenge.codecompiler.api.dto.ContainerExecutionResult;
import tsystems.tchallenge.codecompiler.domain.models.CodeLanguage;
import tsystems.tchallenge.codecompiler.managers.resources.DockerfileType;
import tsystems.tchallenge.codecompiler.managers.resources.ResourceManager;
import tsystems.tchallenge.codecompiler.reliability.exceptions.OperationException;
import tsystems.tchallenge.codecompiler.reliability.exceptions.OperationExceptionBuilder;

import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;

import static com.spotify.docker.client.DockerClient.LogsParam.stderr;
import static com.spotify.docker.client.DockerClient.LogsParam.stdout;
import static com.spotify.docker.client.messages.HostConfig.Bind.from;
import static java.lang.Runtime.getRuntime;
import static java.time.temporal.ChronoUnit.MILLIS;
import static tsystems.tchallenge.codecompiler.managers.docker.ContainerOption.*;

@Service
public class DockerContainerManager {

    private final DockerImageManager dockerImageManager;
    private final DockerClient docker;
    private final ResourceManager resourceManager;
    private final ThreadPoolTaskScheduler scheduler;

    public DockerContainerManager(DockerImageManager dockerImageManager, DockerClient docker,
                                  ResourceManager resourceManager) {
        this.dockerImageManager = dockerImageManager;
        this.docker = docker;
        this.resourceManager = resourceManager;
        this.scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(getRuntime().availableProcessors());
        scheduler.initialize();

    }

    public ContainerExecutionResult startContainer(Path workDir, DockerfileType dockerfileType,
                                                   Option... options)
            throws Exception {

        ContainerOptionsState optionsState = ContainerOptionsState.applyOptions(options);
        CodeLanguage language = languageByWorkDir(workDir);
        HostConfig hostConfig = getHostConfig(workDir, optionsState.bytes, optionsState.volumeReadOnly);


        final ContainerConfig containerConfig = ContainerConfig.builder()
                .hostConfig(hostConfig)
                .image(dockerImageManager.getImageId(language, dockerfileType))
                .build();

        ContainerCreation creation = docker.createContainer(containerConfig);
        String id = creation.id();
        docker.startContainer(id);

        killIfTimeLimitExceeds(id, optionsState.millis);
        Long memUsage = docker.stats(id).memoryStats().maxUsage();
        docker.waitContainer(id);

        return buildResult(id, memUsage);
    }

    private ContainerExecutionResult buildResult(String id, Long memUsage) throws Exception {

        String stdout = collectLogs(id, stdout());
        String stderr = collectLogs(id, stderr());
        ContainerState state = docker.inspectContainer(id).state();
        Long exitCode = state.exitCode();
        Duration executionTime = Duration.between(state.startedAt().toInstant(), Instant.now());
        return ContainerExecutionResult.builder()
                .stdout(stdout)
                .oomKilled(state.oomKilled())
                .stderr(stderr)
                .executionTime(executionTime)
                .exitCode(exitCode)
                .memoryUsage(memUsage)
                .build();

    }

    private String collectLogs(String id, DockerClient.LogsParam... params) throws Exception {
        try (LogStream stream = docker.logs(id, params)) {
            return stream.readFully();
        }

    }

    private void killIfTimeLimitExceeds(String id, Long millis) {
        scheduler.schedule(() -> {
            try {
                ContainerState containerState = docker.inspectContainer(id).state();
                if (containerState.running() || containerState.paused()) {
                    docker.killContainer(id);
                }
            } catch (Exception e) {
                throw wrapped(e);
            }
        }, Instant.now().plus(millis, MILLIS));

    }

    private CodeLanguage languageByWorkDir(Path workDir) {
        Path codeDir = workDir.getParent();
        Path langDir = codeDir.getParent();
        String langDirName = langDir.getFileName().toString();
        return CodeLanguage.valueOf(langDirName.toUpperCase());
    }


    // Create bind between container file system and host file system (see docker volumes)
    private HostConfig getHostConfig(Path workDir, Long memoryLimit, boolean readOnly) {
        return HostConfig.builder()
                .memory(memoryLimit)
                .memorySwappiness(0)
                .appendBinds(from(workDir.toAbsolutePath().toString())
                        .to(resourceManager.codeSuffix)
                        .readOnly(readOnly)
                        .build())
                .build();
    }


    public static class Option {

        ContainerOption option;
        Object value;

        private Option(ContainerOption option, Object value) {
            this.option = option;
            this.value = value;
        }

        public static Option volumeReadOnly() {
            return new Option(VOLUME_READ_ONLY, true);
        }

        public static Option volumeWritable() {
            return new Option(VOLUME_WRITABLE, true);
        }

        public static Option timeLimit(Long milis) {
            return new Option(TIME_LIMIT, milis);
        }

        public static Option memoryLimit(Long kb) {
            return new Option(MEMORY_LIMIT, kb);
        }
    }

    private OperationException wrapped(Exception e) {
        return OperationExceptionBuilder.internal(e);
    }


}
