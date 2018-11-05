package ru.tsystems.tchallenge.codemaster.service.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ContainerOptionsState {
    boolean volumeReadOnly = true;
    boolean networkEnabled = false;
    Integer millis = 5_000;
    Integer bytes = 256 * 1024 * 1024;

    private ContainerOptionsState() {

    }

    public static ContainerOptionsState defaultState() {
        return new ContainerOptionsState();
    }

    public static ContainerOptionsState applyOptions(ContainerOption... options) {
        ContainerOptionsState state = defaultState();
        for (ContainerOption option: options) {
            switch (option.option) {
                case VOLUME_WRITABLE:
                    state.volumeReadOnly = !(boolean) option.value;
                    break;
                case MEMORY_LIMIT:
                    state.bytes = (Integer) option.value;
                    break;
                case TIME_LIMIT:
                    state.millis = (Integer) option.value;
                    break;
                case NETWORK_ENABLED:
                    state.networkEnabled = (boolean) option.value;

            }
        }

        return state;
    }
}
