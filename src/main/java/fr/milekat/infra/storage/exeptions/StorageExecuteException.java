package fr.milekat.infra.storage.exeptions;

import fr.milekat.infra.Main;

public class StorageExecuteException extends Exception {
    private final String message;

    /**
     * Issue during a storage execution
     */
    public StorageExecuteException(Throwable exception, String message) {
        super(exception);
        this.message = message;
        if (Main.DEBUG) {
            exception.printStackTrace();
        }
    }

    /**
     * Get error message (If exist)
     */
    public String getMessage() {
        return message;
    }
}
