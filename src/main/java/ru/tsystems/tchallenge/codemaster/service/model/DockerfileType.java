package ru.tsystems.tchallenge.codemaster.service.model;

public enum DockerfileType {
    COMPILATION("/compilation"),
    RUNNING("/running");

    public String suffixPath;

    DockerfileType(String suffixPath) {
        this.suffixPath = suffixPath;
    }
}
