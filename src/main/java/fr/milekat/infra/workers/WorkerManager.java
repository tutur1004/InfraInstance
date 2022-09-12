package fr.milekat.infra.workers;

import fr.milekat.infra.Main;
import fr.milekat.infra.api.classes.Instance;
import fr.milekat.infra.api.classes.ServerType;
import fr.milekat.infra.storage.exeptions.StorageExecuteException;
import fr.milekat.infra.workers.host.listeners.GameEvents;
import fr.milekat.infra.workers.host.listeners.GameState;
import org.bukkit.Bukkit;

public class WorkerManager {
    public WorkerManager() {
        if (Main.SERVER_TYPE.equals(ServerType.HOST)) {
            try {
                Instance thisHost = Main.getStorage().getInstance(Main.INSTANCE_ID);
                Bukkit.getPluginManager().registerEvents(new GameState(thisHost), Main.getInstance());
                Bukkit.getPluginManager().registerEvents(new GameEvents(), Main.getInstance());
            } catch (StorageExecuteException exception) {
                Main.getOwnLogger().warning("Error while trying to load");
            }
        }
    }
}
