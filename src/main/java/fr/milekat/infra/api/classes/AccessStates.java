package fr.milekat.infra.api.classes;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

public enum AccessStates {
    PRIVATE(0),
    REQUEST_TO_JOIN(1),
    OPEN(2);

    private final int access;

    AccessStates(int access) {
        this.access = access;
    }

    @Contract(pure = true)
    public static @Nullable AccessStates fromInteger(int access) {
        for (AccessStates e : values()) {
            if (e.access == access) {
                return e;
            }
        }
        return null;
    }

    public int getAccess() {
        return access;
    }
}