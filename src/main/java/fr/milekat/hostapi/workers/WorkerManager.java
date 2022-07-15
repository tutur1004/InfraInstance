package fr.milekat.hostapi.workers;

import fr.milekat.hostapi.Main;
import fr.milekat.hostapi.api.classes.ServerType;
import org.bukkit.Bukkit;

public class WorkerManager {
    public WorkerManager() {
        if (Main.SERVER_TYPE.equals(ServerType.LOBBY) || Main.SERVER_TYPE.equals(ServerType.HOST)) {
            Bukkit.getServer().getMessenger().registerOutgoingPluginChannel(Main.getInstance(), Main.MESSAGE_CHANNEL);
        }

        if (Main.SERVER_TYPE.equals(ServerType.HOST)) {
            Bukkit.getPluginManager().registerEvents(new GameState(), Main.getInstance());
            Bukkit.getPluginManager().registerEvents(new GameEvents(), Main.getInstance());
            Bukkit.getServer().getMessenger().registerIncomingPluginChannel
                    (Main.getInstance(), Main.MESSAGE_CHANNEL, new HostProxyReceive());
        }
    }
}
