package fr.milekat.infra.messaging.exeptions;

public class MessagingLoaderException extends Throwable {
    public MessagingLoaderException(String errorMessage) {
        super(errorMessage);
    }
}
