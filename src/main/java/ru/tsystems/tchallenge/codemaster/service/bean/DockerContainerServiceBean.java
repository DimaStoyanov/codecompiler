package ru.tsystems.tchallenge.codemaster.service.bean;

import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.LogStream;
import com.spotify.docker.client.exceptions.ContainerNotFoundException;
import com.spotify.docker.client.exceptions.DockerException;
import com.spotify.docker.client.messages.ContainerConfig;
import com.spotify.docker.client.messages.ContainerCreation;
import com.spotify.docker.client.messages.ContainerState;
import com.spotify.docker.client.messages.HostConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;
import ru.tsystems.tchallenge.codemaster.service.model.ContainerExecutionResult;
import ru.tsystems.tchallenge.codemaster.domain.models.CodeLanguage;
import ru.tsystems.tchallenge.codemaster.service.DockerContainerService;
import ru.tsystems.tchallenge.codemaster.service.DockerImageService;
import ru.tsystems.tchallenge.codemaster.service.model.ContainerOption;
import ru.tsystems.tchallenge.codemaster.service.model.ContainerOptionsState;
import ru.tsystems.tchallenge.codemaster.service.model.DockerfileType;

import javax.annotation.PostConstruct;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

import static com.spotify.docker.client.DockerClient.LogsParam.stderr;
import static com.spotify.docker.client.DockerClient.LogsParam.stdout;
import static com.spotify.docker.client.messages.HostConfig.Bind.from;
import static java.lang.Runtime.getRuntime;
import static java.time.temporal.ChronoUnit.MILLIS;
import static ru.tsystems.tchallenge.codemaster.reliability.OperationExceptionBuilder.internal;

@RequiredArgsConstructor
@Service
@Log4j2
public class DockerContainerServiceBean implements DockerContainerService  {

    private final DockerImageService dockerImageService;
    private final DockerClient docker;
    private final ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();

    private final Map<String, Long> containerMemStat = new ConcurrentHashMap<>();


    @PostConstruct
    public void init() {
        scheduler.setPoolSize(getRuntime().availableProcessors());
        scheduler.initialize();
    }

    @Override
    public ContainerExecutionResult executeContainer(Path workDir, DockerfileType dockerfileType,
                                                   ContainerOption... options)
            throws Exception {

        ContainerOptionsState optionsState = ContainerOptionsState.applyOptions(options);
        CodeLanguage language = languageByWorkDir(workDir);
        HostConfig hostConfig = getHostConfig(workDir, optionsState.getBytes(), optionsState.isVolumeReadOnly());


        final ContainerConfig containerConfig = ContainerConfig.builder()
                .hostConfig(hostConfig)
                .networkDisabled(!optionsState.isNetworkEnabled())
                .image(dockerImageService.getImageId(language, dockerfileType))
                .build();

        ContainerCreation creation = docker.createContainer(containerConfig);
        String id = creation.id();
        docker.startContainer(id);

        killIfTimeLimitExceeds(id, optionsState.getMillis());
        docker.waitContainer(id);
        ContainerExecutionResult result = buildResult(id);
        docker.removeContainer(id);
        return result;
    }

    private ContainerExecutionResult buildResult(String id) throws Exception {

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
                .build();

    }

    private String collectLogs(String id, DockerClient.LogsParam... params) throws Exception {
        try (LogStream stream = docker.logs(id, params)) {
            return stream.readFully();
        }

    }

    private void killIfTimeLimitExceeds(String id, Integer millis) {
        scheduler.schedule(() -> {
            try {
                ContainerState containerState = docker.inspectContainer(id).state();
                if (containerState.running() || containerState.paused()) {
                    docker.killContainer(id);
                }
            } catch (ContainerNotFoundException e) {
                // Suppress this exception, because container is already removed, nothing to kill
            } catch (Exception e) {
                throw internal(e);
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
    private HostConfig getHostConfig(Path workDir, Integer memoryLimit, boolean readOnly) {
        return HostConfig.builder()
                .memory(Long.valueOf(memoryLimit))
                .memorySwappiness(0)
                .appendBinds(from(workDir.toAbsolutePath().toString())
                        .to("/code")
                        .readOnly(readOnly)
                        .build())
                .build();
    }

}
