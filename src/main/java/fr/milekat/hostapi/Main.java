package fr.milekat.hostapi;

import fr.milekat.hostapi.api.classes.ServerType;
import fr.milekat.hostapi.messaging.Messaging;
import fr.milekat.hostapi.messaging.MessagingManager;
import fr.milekat.hostapi.messaging.exeptions.MessagingLoaderException;
import fr.milekat.hostapi.storage.StorageExecutor;
import fr.milekat.hostapi.storage.StorageManager;
import fr.milekat.hostapi.storage.exeptions.StorageLoaderException;
import fr.milekat.hostapi.workers.WorkerManager;
import fr.milekat.hostapi.workers.host.HostAccess;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

public class Main extends JavaPlugin {
    public static final String HOST_UUID_ENV_VAR_NAME = "HOST_UUID";
    public static Player HOST_PLAYER;
    public static HostAccess HOST_ACCESS = new HostAccess();
    public static Map<String, UUID> WHITE_LIST = new HashMap<>();
    public static Map<String, UUID> WAIT_LIST = new HashMap<>();

    public static final Integer INSTANCE_ID = Integer.parseInt(
            System.getenv("INSTANCE_ID") == null ? "0" : System.getenv("INSTANCE_ID"));
    public static final String SERVER_ID = System.getenv("SERVER_ID");
    public static final String SERVER_NAME = System.getenv("SERVER_NAME");
    public static final String MESSAGE_CHANNEL = "HOST_MESSAGING";
    public static final String GAME = System.getenv("GAME");
    public static final String VERSION = System.getenv("VERSION");

    private static JavaPlugin plugin;
    private static FileConfiguration configFile;
    public static ServerType SERVER_TYPE;
    public static Boolean DEBUG = false;
    private static StorageManager LOADED_STORAGE;
    private static MessagingManager LOADED_MESSAGING;

    @Override
    public void onEnable() {
        plugin = this;
        configFile = this.getConfig();
        DEBUG = configFile.getBoolean("debug");
        SERVER_TYPE = ServerType.valueOf(configFile.getString("server-type").toUpperCase(Locale.ROOT));
        if (DEBUG) getHostLogger().info("Debug enable");
        getHostLogger().info("Server type: " + SERVER_TYPE.name());
        //  Load storage
        try {
            LOADED_STORAGE = new StorageManager(configFile);
            if (DEBUG) {
                getHostLogger().info("Storage enable, API is now available");
            }
        } catch (StorageLoaderException exception) {
            getHostLogger().warning("Storage load failed, disabling plugin..");
            this.onDisable();
            if (DEBUG) {
                exception.printStackTrace();
            } else {
                getHostLogger().warning("Error: " + exception.getLocalizedMessage());
            }
        }
        new WorkerManager();
        //  Load messaging (Optional since this plugin can be used only as an API)
        try {
            LOADED_MESSAGING = new MessagingManager(configFile);
        } catch (MessagingLoaderException exception) {
            getHostLogger().warning("Messaging load failed, disabling plugin..");
            getHostLogger().warning("If you only need the API, set messaging.type to 'none'.");
            this.onDisable();
            if (DEBUG) {
                exception.printStackTrace();
            } else {
                getHostLogger().warning("Error: " + exception.getLocalizedMessage());
            }
        }
    }

    @Override
    public void onDisable() {
        getStorage().disconnect();
        getMessaging().disconnect();
    }

    /**
     * Use plugin logger
     * @return Custom logger
     */
    public static Logger getHostLogger() {
        return plugin.getLogger();
    }

    /**
     * Get Storage Database Executor
     * @return Storage executor
     */
    public static StorageExecutor getStorage() {
        return LOADED_STORAGE.getStorageExecutor();
    }

    /**
     * Get Messaging
     * @return Messaging interface
     */
    public static Messaging getMessaging() {
        return LOADED_MESSAGING.getMessaging();
    }

    /**
     * Get config file
     * @return Config file
     */
    public static FileConfiguration getFileConfig() {
        return configFile;
    }

    /**
     * Get the plugin instance
     * @return bungee-cord plugin instance
     */
    public static JavaPlugin getInstance() {
        return plugin;
    }
}
