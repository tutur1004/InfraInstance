package fr.milekat.infra.api.classes;

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

    public static InstanceState fromInteger(int stateId) {
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
