package fr.milekat.hostapi.workers;

import fr.milekat.hostapi.Main;
import fr.milekat.hostapi.api.classes.Instance;
import fr.milekat.hostapi.api.classes.ServerType;
import fr.milekat.hostapi.storage.exeptions.StorageExecuteException;
import fr.milekat.hostapi.workers.host.GameEvents;
import fr.milekat.hostapi.workers.host.GameState;
import fr.milekat.hostapi.workers.host.HostProxyReceive;
import org.bukkit.Bukkit;

public class WorkerManager {
    public WorkerManager() {
        if (Main.SERVER_TYPE.equals(ServerType.LOBBY) || Main.SERVER_TYPE.equals(ServerType.HOST)) {
            Bukkit.getServer().getMessenger().registerOutgoingPluginChannel(Main.getInstance(), Main.MESSAGE_CHANNEL);
        }
        if (Main.SERVER_TYPE.equals(ServerType.HOST)) {
            try {
                Instance thisHost = Main.getStorage().getInstance(Integer.parseInt(System.getenv(Main.INSTANCE_ID)));
                Bukkit.getPluginManager().registerEvents(new GameState(thisHost), Main.getInstance());
                Bukkit.getPluginManager().registerEvents(new GameEvents(), Main.getInstance());
                Bukkit.getServer().getMessenger().registerIncomingPluginChannel
                        (Main.getInstance(), Main.MESSAGE_CHANNEL, new HostProxyReceive());
            } catch (StorageExecuteException exception) {
                Main.getHostLogger().warning("Error while trying to load");
                return;
            }
        }
    }
}
