package fr.milekat.infra.storage;

import fr.milekat.infra.Main;
import fr.milekat.infra.api.classes.Game;
import fr.milekat.infra.api.classes.Instance;
import fr.milekat.infra.api.classes.User;
import fr.milekat.infra.storage.adapter.sql.SQLStorage;
import fr.milekat.infra.storage.exeptions.StorageExecuteException;
import fr.milekat.infra.storage.exeptions.StorageLoaderException;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class Storage {
    public static final long GAMES_DELAY = TimeUnit.MILLISECONDS.convert(10L, TimeUnit.MINUTES);
    public static Map<Game, Date> GAMES_CACHE = new HashMap<>();
    public static final long INSTANCES_DELAY = TimeUnit.MILLISECONDS.convert(2L, TimeUnit.SECONDS);
    public static Map<Instance, Date> INSTANCES_CACHE = new HashMap<>();
    public static final long USERS_DELAY = TimeUnit.MILLISECONDS.convert(1L, TimeUnit.SECONDS);
    public static Map<User, Date> USERS_CACHE = new HashMap<>();

    private final StorageImplementation executor;

    public Storage(@NotNull FileConfiguration config) throws StorageLoaderException {
        String storageType = config.getString("storage.type");
        if (Main.DEBUG) {
            Main.getOwnLogger().info("Loading storage type: " + storageType);
        }
        switch (storageType.toLowerCase()) {
            case "mysql":
            case "mariadb":
            case "postgres": {
                executor = new SQLStorage(config);
                break;
            }
            default: throw new StorageLoaderException("Unsupported storage type");
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

    public StorageImplementation getStorageImplementation() {
        return this.executor;
    }
}
