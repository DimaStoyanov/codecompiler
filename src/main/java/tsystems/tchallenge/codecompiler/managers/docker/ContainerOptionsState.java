package tsystems.tchallenge.codecompiler.managers.docker;

import tsystems.tchallenge.codecompiler.managers.docker.DockerContainerManager.Option;

public class ContainerOptionsState {


    public boolean volumeReadOnly = true;

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
            }
        }

        return state;
    }
}
