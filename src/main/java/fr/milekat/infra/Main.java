package fr.milekat.infra;

import fr.milekat.infra.api.classes.Instance;
import fr.milekat.infra.api.classes.ServerType;
import fr.milekat.infra.messaging.Messaging;
import fr.milekat.infra.messaging.MessagingImplementation;
import fr.milekat.infra.messaging.exeptions.MessagingLoaderException;
import fr.milekat.infra.messaging.exeptions.MessagingSendException;
import fr.milekat.infra.messaging.sending.MessageToProxy;
import fr.milekat.infra.storage.Storage;
import fr.milekat.infra.storage.StorageImplementation;
import fr.milekat.infra.storage.exeptions.StorageLoaderException;
import fr.milekat.infra.workers.WorkerManager;
import fr.minuskube.inv.InventoryManager;
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
    public static final String HOST_FREE_ENV_VAR_NAME = "HOST_FREE";
    public static final String PROXY_PREFIX = "proxy";
    public static final String LOBBY_PREFIX = "lobby";
    public static final String HOST_PREFIX = "host";
    public static Player HOST_PLAYER;
    public static Instance HOST_INSTANCE;
    public static Map<UUID, String> WHITE_LIST = new HashMap<>();
    public static Map<UUID, String> WAIT_LIST = new HashMap<>();

    public static final Integer INSTANCE_ID =
            Integer.parseInt(System.getenv("INSTANCE_ID") == null ? "0" : System.getenv("INSTANCE_ID"));
    public static final String SERVER_ID = System.getenv("SERVER_ID");
    public static final String SERVER_NAME = System.getenv("SERVER_NAME");
    public static final String GAME = System.getenv("GAME");
    public static final String VERSION = System.getenv("VERSION");

    private static JavaPlugin plugin;
    public static InventoryManager INVENTORY_MANAGER;
    private static FileConfiguration configFile;
    public static ServerType SERVER_TYPE;
    public static Boolean DEBUG = false;
    private static Storage LOADED_STORAGE;
    private static Messaging LOADED_MESSAGING;

    @Override
    public void onEnable() {
        plugin = this;
        INVENTORY_MANAGER = new InventoryManager(plugin);
        INVENTORY_MANAGER.init();
        configFile = this.getConfig();
        DEBUG = configFile.getBoolean("debug");
        SERVER_TYPE = ServerType.valueOf(configFile.getString("server-type").toUpperCase(Locale.ROOT));
        if (DEBUG) getOwnLogger().info("Debug enable");
        getOwnLogger().info("Server type: " + SERVER_TYPE.name());
        //  Load storage
        try {
            LOADED_STORAGE = new Storage(configFile);
            if (DEBUG) {
                getOwnLogger().info("Storage enable, API is now available");
            }
        } catch (StorageLoaderException exception) {
            getOwnLogger().warning("Storage load failed, disabling plugin..");
            this.onDisable();
            if (DEBUG) {
                exception.printStackTrace();
            } else {
                getOwnLogger().warning("Error: " + exception.getLocalizedMessage());
            }
        }
        //  Load messaging (Optional since this plugin can be used only as an API)
        try {
            LOADED_MESSAGING = new Messaging(configFile);
        } catch (MessagingLoaderException exception) {
            getOwnLogger().warning("Messaging load failed, disabling plugin..");
            getOwnLogger().warning("If you only need the API, set messaging.type to 'none'.");
            if (DEBUG) {
                exception.printStackTrace();
            } else {
                getOwnLogger().warning("Error: " + exception.getLocalizedMessage());
            }
            this.onDisable();
        }
        new WorkerManager(this);
    }

    @Override
    public void onDisable() {
        if (Main.getFileConfig().getBoolean("auto-terminate")) {
            try {
                MessageToProxy.notifyGameFinish();
            } catch (MessagingSendException exception) {
                if (Main.DEBUG) {
                    getOwnLogger().warning("Error while trying to notify Game Finish");
                    exception.printStackTrace();
                }
            }
        }
        try {
            getStorage().disconnect();
        } catch (Exception ignored) {}
        try {
            getMessaging().disconnect();
        } catch (Exception ignored) {}
    }

    /**
     * Use plugin logger
     * @return Custom logger
     */
    public static Logger getOwnLogger() {
        return plugin.getLogger();
    }

    /**
     * Get Storage
     * @return Storage implementation
     */
    public static StorageImplementation getStorage() {
        return LOADED_STORAGE.getStorageImplementation();
    }

    /**
     * Get Messaging
     * @return Messaging interface
     */
    public static MessagingImplementation getMessaging() {
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
     * @return bukkit plugin instance
     */
    public static JavaPlugin getInstance() {
        return plugin;
    }
}
