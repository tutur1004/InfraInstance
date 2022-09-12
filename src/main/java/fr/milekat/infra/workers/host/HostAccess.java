package fr.milekat.infra.workers.host;

public class HostAccess {
    private AccessStates access;

    public HostAccess() {
        this.access = AccessStates.PRIVATE;
    }

    public AccessStates getAccess() {
        return access;
    }

    public void setAccess(AccessStates access) {
        this.access = access;
    }

    public enum AccessStates {
        OPEN,
        REQUEST_TO_JOIN,
        PRIVATE
    }
}
