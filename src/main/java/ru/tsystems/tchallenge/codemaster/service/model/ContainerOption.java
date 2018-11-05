package ru.tsystems.tchallenge.codemaster.service.model;


public class ContainerOption {
    public final Option option;
    public final Object value;

    private ContainerOption(Option option, Object value) {
        this.option = option;
        this.value = value;
    }

    public static ContainerOption volumeWritable() {
        return new ContainerOption(Option.VOLUME_WRITABLE, true);
    }

    public static ContainerOption timeLimit(Integer millis) {
        return new ContainerOption(Option.TIME_LIMIT, millis);
    }

    public static ContainerOption memoryLimit(Integer kb) {
        return new ContainerOption(Option.MEMORY_LIMIT, kb);
    }

    public static ContainerOption networkEnabled(Boolean enabled) {
        return new ContainerOption(Option.NETWORK_ENABLED, enabled);
    }

    enum Option {
        VOLUME_WRITABLE, TIME_LIMIT, MEMORY_LIMIT, NETWORK_ENABLED
    }
}


