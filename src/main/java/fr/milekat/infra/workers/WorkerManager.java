package fr.milekat.infra.workers;

import fr.milekat.infra.Main;
import fr.milekat.infra.api.classes.Instance;
import fr.milekat.infra.api.classes.ServerType;
import fr.milekat.infra.storage.exeptions.StorageExecuteException;
import fr.milekat.infra.workers.host.commands.OpenMainGui;
import fr.milekat.infra.workers.host.listeners.GameEvents;
import fr.milekat.infra.workers.host.listeners.GameState;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class WorkerManager {
    public WorkerManager(JavaPlugin plugin) {
        if (Main.SERVER_TYPE.equals(ServerType.HOST)) {
            try {
                Instance thisHost = Main.getStorage().getInstance(Main.INSTANCE_ID);
                Bukkit.getPluginManager().registerEvents(new GameState(thisHost), Main.getInstance());
                Bukkit.getPluginManager().registerEvents(new GameEvents(), Main.getInstance());
                plugin.getCommand("host").setExecutor(new OpenMainGui());
            } catch (StorageExecuteException exception) {
                Main.getOwnLogger().warning("Error while trying to load");
            }
        }
    }
}