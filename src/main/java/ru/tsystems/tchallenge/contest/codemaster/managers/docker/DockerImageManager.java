package ru.tsystems.tchallenge.contest.codemaster.managers.docker;

import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.exceptions.DockerException;
import com.spotify.docker.client.exceptions.ImageNotFoundException;
import com.spotify.docker.client.messages.ImageInfo;
import com.spotify.docker.client.messages.RegistryAuth;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.tsystems.tchallenge.contest.codemaster.domain.models.CodeLanguage;
import ru.tsystems.tchallenge.contest.codemaster.managers.resources.DockerfileType;
import ru.tsystems.tchallenge.contest.codemaster.managers.resources.ServiceResourceManager;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Path;
import java.util.EnumMap;
import java.util.Optional;

import static ru.tsystems.tchallenge.contest.codemaster.managers.resources.DockerfileType.COMPILATION;
import static ru.tsystems.tchallenge.contest.codemaster.managers.resources.DockerfileType.RUNNING;
import static ru.tsystems.tchallenge.contest.codemaster.reliability.OperationExceptionBuilder.internal;

@Service
@Log4j2
public class DockerImageManager {

    private final DockerClient docker;
    private final ServiceResourceManager resourceManager;
    private final EnumMap<CodeLanguage, String> compileImageByLang;
    private final EnumMap<CodeLanguage, String> runImageByLang;

    @Value("${docker.username}")
    private String dockerUsername;
    @Value("${docker.password}")
    private String dockerPassword;

    @Autowired
    public DockerImageManager(DockerClient docker, ServiceResourceManager resourceManager) {
        this.docker = docker;
        this.resourceManager = resourceManager;
        this.compileImageByLang = new EnumMap<>(CodeLanguage.class);
        runImageByLang = new EnumMap<>(CodeLanguage.class);
    }


    @PostConstruct
    public void init() {
        for (CodeLanguage lang : CodeLanguage.values()) {
            compileImageByLang.put(lang, getImage(lang, COMPILATION));
            runImageByLang.put(lang, getImage(lang, RUNNING));
        }
    }


    String getImageId(CodeLanguage language, DockerfileType type) {
        switch (type) {
            case COMPILATION:
                return compileImageByLang.get(language);
            case RUNNING:
                return runImageByLang.get(language);
            default:
                throw new UnsupportedOperationException("Dockerfile type " + type
                        + " not supported");
        }
    }

    private String getImage(CodeLanguage language, DockerfileType type) {
        String imageName = (dockerUsername + "/" + language+ "_" + type).toLowerCase();
        return getImageFromRegistry(imageName)
                .or(() -> pullImage(imageName))
                .orElseGet(() -> pushImage(createImage(language, type, imageName)));
    }

    private Optional<String> getImageFromRegistry(@NonNull String imageName) {
        try {
            ImageInfo imageInfo = docker.inspectImage(imageName);
            log.info(String.format("Get image %s from registry", imageName));
            return Optional.of(imageName);
        } catch (ImageNotFoundException e) {
            return Optional.empty();
        } catch (Exception e) {
            throw internal(e);
        }
    }

    private Optional<String> pullImage(String imageName) {
        try {
            docker.pull(imageName);
            log.info(String.format("Successfully pull image %s", imageName));
            return Optional.of(imageName);
        } catch (ImageNotFoundException e) {
            return Optional.empty();
        } catch (Exception e) {
            throw internal(e);
        }
    }


    private String createImage(CodeLanguage language, DockerfileType type, String imageName) {
        Path dockerFileDir = resourceManager.getDockerfileDir(language, type);
        try {
            docker.build(dockerFileDir, imageName);
            log.info("Successfully created image " + imageName);
            return imageName;
        } catch (DockerException | InterruptedException | IOException e) {
            throw new IllegalStateException("Can not build docker image", e);
        }
    }

    private String pushImage(String imageName) {
        try {
            RegistryAuth auth = RegistryAuth.builder()
                    .username(dockerUsername)
                    .password(dockerPassword)
                    .build();
            docker.push(imageName, auth);
            log.info("Successfully pushed image " + imageName);
        } catch (DockerException | InterruptedException e) {
            log.info(String.format("Unable to push image %s, use registry image", imageName));
            log.error(e);
        }
        return imageName;
    }


}
