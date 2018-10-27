package tsystems.tchallenge.codecompiler.config.docker;

import com.spotify.docker.client.DefaultDockerClient;
import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.exceptions.DockerCertificateException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DockerConfig {

    @Value("${docker.host}")
    private String dockerHost;

    @Bean
    public DockerClient dockerClient() throws DockerCertificateException {
        return DefaultDockerClient
                .fromEnv().build();
    }
}
