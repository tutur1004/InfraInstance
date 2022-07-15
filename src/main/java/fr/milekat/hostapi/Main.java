package fr.milekat.hostapi;

import fr.milekat.hostapi.api.classes.Instance;
import fr.milekat.hostapi.api.classes.ServerType;
import fr.milekat.hostapi.storage.StorageExecutor;
import fr.milekat.hostapi.storage.StorageManager;
import fr.milekat.hostapi.storage.exeptions.StorageExecuteException;
import fr.milekat.hostapi.storage.exeptions.StorageLoaderException;
import fr.milekat.hostapi.workers.WorkerManager;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Locale;
import java.util.logging.Logger;

public class Main extends JavaPlugin {
    public static String HOST_UUID_ENV_VAR_NAME = "HOST_UUID";
    public static String SERVER_NAME = "SERVER_NAME";
    public static String SERVER_ID = "SERVER_ID";
    public static String MESSAGE_CHANNEL = "host:channel";

    private static JavaPlugin plugin;
    private static FileConfiguration configFile;
    public static ServerType SERVER_TYPE;
    public static Boolean DEBUG = false;
    private static StorageManager LOADED_STORAGE;

    @Override
    public void onEnable() {
        plugin = this;
        configFile = this.getConfig();
        DEBUG = configFile.getBoolean("debug");
        SERVER_TYPE = ServerType.valueOf(configFile.getString("server-type").toUpperCase(Locale.ROOT));
        if (DEBUG) getHostLogger().info("Debug enable");
        getHostLogger().info("Server type: " + SERVER_TYPE.name());
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
        if (SERVER_TYPE.equals(ServerType.HOST)) {
            SERVER_NAME = this.getServer().getServerName();
            try {
                Instance instance = getStorage().getInstance(SERVER_NAME);
                if (instance==null) {
                    throw new StorageLoaderException("Server instance not found in storage.");
                }
                SERVER_ID = instance.getServerId();
            } catch (StorageLoaderException | StorageExecuteException exception) {
                getHostLogger().warning("Instance load failed, disabling plugin..");
                this.onDisable();
                if (DEBUG) {
                    exception.printStackTrace();
                } else {
                    getHostLogger().warning("Error: " + exception.getLocalizedMessage());
                }
            }
        }
        new WorkerManager();
    }

    @Override
    public void onDisable() {
        this.getServer().getMessenger().unregisterOutgoingPluginChannel(this, MESSAGE_CHANNEL);
        getStorage().disconnect();
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
