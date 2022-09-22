package fr.milekat.infra.api.classes;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

public enum InstanceState {
    CREATING(0),
    READY(1),
    IN_PROGRESS(2),
    ENDING(3),
    TERMINATED(4);

    private final int stateId;

    InstanceState(int stateId) {
        this.stateId = stateId;
    }

    @Contract(pure = true)
    public static @Nullable InstanceState fromInteger(int stateId) {
        for (InstanceState e : values()) {
            if (e.stateId == stateId) {
                return e;
            }
        }
        return null;
    }

    public int getStateId() {
        return stateId;
    }
}
