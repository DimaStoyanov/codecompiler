package com.codecompiler.codecompiler;

import com.spotify.docker.client.*;
import com.spotify.docker.client.exceptions.DockerException;
import com.spotify.docker.client.messages.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.file.Path;
import java.nio.file.Paths;

import static com.spotify.docker.client.DockerClient.LogsParam.stderr;
import static com.spotify.docker.client.DockerClient.LogsParam.stdout;

@SpringBootApplication
public class CodeCompilerApplication {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(CodeCompilerApplication.class, args);

        final DockerClient docker = DefaultDockerClient.fromEnv().build();


        // TODO: change buildArgs to docker.copyToContainer(...)
        final String buildArgs = "{\"file_path\":\"HelloWorld.java\"}";
        Path directory = Paths.get(ClassLoader.getSystemResource("static").toURI());
        String image = docker.build(directory, message -> System.out.println("My output: " + message),
                DockerClient.BuildParam.create("buildargs", URLEncoder.encode(buildArgs, "UTF-8")));

        final ContainerConfig containerConfig = ContainerConfig.builder()
                .image(image)
                .build();

        final ContainerCreation creation = docker.createContainer(containerConfig);
        final String id = creation.id();

        docker.startContainer(id);
        final ContainerInfo info = docker.inspectContainer(id);
        System.out.println(info.state());


        String logs;
        Thread.sleep(1000);
        System.out.println("STDOUT LOGS");
        try (LogStream stream = docker.logs(id, stdout())) {
            logs = stream.readFully();
            System.out.println(logs);
        }

        System.out.println("STDERR LOGS");
        try (LogStream stream = docker.logs(id, stderr())) {
            logs = stream.readFully();
            System.out.println(logs);
        }

        System.out.println(docker.inspectContainer(id).state());

    }
}
