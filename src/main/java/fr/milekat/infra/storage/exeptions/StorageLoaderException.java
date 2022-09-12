package fr.milekat.infra.storage.exeptions;

public class StorageLoaderException extends Throwable {
    public StorageLoaderException(String errorMessage) {
        super(errorMessage);
    }
}
