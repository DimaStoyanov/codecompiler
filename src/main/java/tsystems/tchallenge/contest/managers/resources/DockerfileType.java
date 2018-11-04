package tsystems.tchallenge.contest.managers.resources;

public enum DockerfileType {
    COMPILATION("/compilation"),
    RUNNING("/running");

    String suffixPath;

    DockerfileType(String suffixPath) {
        this.suffixPath = suffixPath;
    }
}
