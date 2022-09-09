package fr.milekat.hostapi.workers;

import fr.milekat.hostapi.Main;
import fr.milekat.hostapi.api.classes.Instance;
import fr.milekat.hostapi.api.classes.ServerType;
import fr.milekat.hostapi.storage.exeptions.StorageExecuteException;
import fr.milekat.hostapi.workers.host.listeners.GameEvents;
import fr.milekat.hostapi.workers.host.listeners.GameState;
import org.bukkit.Bukkit;

public class WorkerManager {
    public WorkerManager() {
        if (Main.SERVER_TYPE.equals(ServerType.HOST)) {
            try {
                Instance thisHost = Main.getStorage().getInstance(Main.INSTANCE_ID);
                Bukkit.getPluginManager().registerEvents(new GameState(thisHost), Main.getInstance());
                Bukkit.getPluginManager().registerEvents(new GameEvents(), Main.getInstance());
            } catch (StorageExecuteException exception) {
                Main.getHostLogger().warning("Error while trying to load");
            }
        }
    }
}
