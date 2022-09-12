package fr.milekat.infra.messaging.exeptions;

import fr.milekat.infra.Main;

public class MessagingSendException extends Exception {
    private final String message;

    /**
     * Issue during a message send
     */
    public MessagingSendException(Throwable exception, String message) {
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
