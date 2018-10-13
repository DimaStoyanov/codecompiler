package tsystems.tchallenge.codecompiler.managers.docker;

import tsystems.tchallenge.codecompiler.managers.docker.DockerContainerManager.Option;

public class ContainerOptionsState {


    public boolean volumeReadOnly = true;
    public Long millis = 5_000L;
    public Long bytes = 256 * 1024 * 1024L;

    private ContainerOptionsState() {

    }

    public static ContainerOptionsState defaultState() {
        return new ContainerOptionsState();
    }

    public static ContainerOptionsState applyOptions(Option... options) {
        ContainerOptionsState state = defaultState();
        for (Option option : options) {
            switch (option.option) {
                case VOLUME_READ_ONLY:
                    state.volumeReadOnly = (boolean) option.value;
                    break;
                case VOLUME_WRITABLE:
                    state.volumeReadOnly = !(boolean) option.value;
                    break;
                case MEMORY_LIMIT:
                    state.bytes = (Long) option.value;
                    break;
                case TIME_LIMIT:
                    state.millis = (Long) option.value;

            }
        }

        return state;
    }
}
