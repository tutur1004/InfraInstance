package fr.milekat.infra.storage;

import fr.milekat.infra.Main;
import fr.milekat.infra.storage.adapter.mysql.MySQLAdapter;
import fr.milekat.infra.storage.exeptions.StorageExecuteException;
import fr.milekat.infra.storage.exeptions.StorageLoaderException;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;

public class StorageManager {
    private final StorageExecutor executor;

    public StorageManager(@NotNull FileConfiguration config) throws StorageLoaderException {
        String storageType = config.getString("storage.type");
        if (Main.DEBUG) {
            Main.getOwnLogger().info("Loading storage type: " + storageType);
        }
        if (storageType.equalsIgnoreCase("mysql") || storageType.equalsIgnoreCase("mariadb")) {
            executor = new MySQLAdapter(config);
        } else {
            throw new StorageLoaderException("Unsupported storage type");
        }
        try {
            if (executor.checkStorages()) {
                if (Main.DEBUG) {
                    Main.getOwnLogger().info("Storage loaded");
                }
            } else {
                throw new StorageLoaderException("Storages are not loaded properly");
            }
        } catch (StorageExecuteException exception) {
            throw new StorageLoaderException("Can't load storage properly");
        }
    }

    public StorageExecutor getStorageExecutor() {
        return this.executor;
    }
}
