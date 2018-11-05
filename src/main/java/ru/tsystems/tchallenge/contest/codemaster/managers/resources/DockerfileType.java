package ru.tsystems.tchallenge.contest.codemaster.managers.resources;

public enum DockerfileType {
    COMPILATION("/compilation"),
    RUNNING("/running");

    String suffixPath;

    DockerfileType(String suffixPath) {
        this.suffixPath = suffixPath;
    }
}
