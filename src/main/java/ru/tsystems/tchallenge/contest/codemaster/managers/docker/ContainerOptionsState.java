package ru.tsystems.tchallenge.contest.codemaster.managers.docker;

import ru.tsystems.tchallenge.contest.codemaster.managers.docker.DockerContainerManager.Option;

public class ContainerOptionsState {
    boolean volumeReadOnly = true;
    boolean networkEnabled = false;
    Long millis = 5_000L;
    Long bytes = 256 * 1024 * 1024L;

    private ContainerOptionsState() {

    }

    public static ContainerOptionsState defaultState() {
        return new ContainerOptionsState();
    }

    public static ContainerOptionsState applyOptions(Option... options) {
        ContainerOptionsState state = defaultState();
        for (Option option : options) {
            switch (option.option) {
                case VOLUME_WRITABLE:
                    state.volumeReadOnly = !(boolean) option.value;
                    break;
                case MEMORY_LIMIT:
                    state.bytes = (Long) option.value;
                    break;
                case TIME_LIMIT:
                    state.millis = (Long) option.value;
                    break;
                case NETWORK_ENABLED:
                    state.networkEnabled = (boolean) option.value;

            }
        }

        return state;
    }
}
