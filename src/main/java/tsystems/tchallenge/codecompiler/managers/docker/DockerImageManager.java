package tsystems.tchallenge.codecompiler.managers.docker;

import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.exceptions.DockerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tsystems.tchallenge.codecompiler.domain.models.CodeLanguage;
import tsystems.tchallenge.codecompiler.managers.resources.DockerfileType;
import tsystems.tchallenge.codecompiler.managers.resources.ResourceManager;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Path;
import java.util.EnumMap;

import static tsystems.tchallenge.codecompiler.managers.resources.DockerfileType.COMPILATION;
import static tsystems.tchallenge.codecompiler.managers.resources.DockerfileType.RUNNING;

@Service
public class DockerImageManager {

    private final DockerClient docker;
    private final ResourceManager resourceManager;
    private final EnumMap<CodeLanguage, String> compileImageByLang;
    private final EnumMap<CodeLanguage, String> runImageByLang;


    @Autowired
    public DockerImageManager(DockerClient docker, ResourceManager resourceManager) {
        this.docker = docker;
        this.resourceManager = resourceManager;
        this.compileImageByLang = new EnumMap<>(CodeLanguage.class);
        runImageByLang = new EnumMap<>(CodeLanguage.class);
    }


    @PostConstruct
    public void init() {
        for (CodeLanguage language : CodeLanguage.values()) {
            compileImageByLang.put(language, createImage(language, COMPILATION));
            runImageByLang.put(language, createImage(language, RUNNING));
        }
    }

    public String getImageId(CodeLanguage language, DockerfileType type) {
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


    private String createImage(CodeLanguage language, DockerfileType type) {
        Path dockerFileDir = resourceManager.getDockerfileDir(language, type);
        try {
            return docker.build(dockerFileDir);

        } catch (DockerException | InterruptedException | IOException e) {
            throw new IllegalStateException("Can not build docker image");
        }
    }


}
