package fr.milekat.hostapi.api.classes;

public enum LogAction {
    FETCH_RESOURCES(0),
    GAME_CREATE(1),
    GAME_READY(2),
    GAME_START(3),
    GAME_END(4),
    INSTANCE_DELETE(5);

    private final int action;

    LogAction(int action) {
        this.action = action;
    }

    public static LogAction fromInteger(int action) {
        for (LogAction e : values()) {
            if (e.action == action) {
                return e;
            }
        }
        return null;
    }
}
