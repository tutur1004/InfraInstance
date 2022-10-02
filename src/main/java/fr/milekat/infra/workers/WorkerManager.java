package fr.milekat.infra.workers;

import fr.milekat.infra.Main;
import fr.milekat.infra.api.classes.InstanceState;
import fr.milekat.infra.api.classes.ServerType;
import fr.milekat.infra.messaging.exeptions.MessagingSendException;
import fr.milekat.infra.messaging.sending.MessageToProxy;
import fr.milekat.infra.storage.exeptions.StorageExecuteException;
import fr.milekat.infra.workers.host.commands.OpenHostMainGui;
import fr.milekat.infra.workers.host.listeners.GameEvents;
import fr.milekat.infra.workers.host.listeners.GameState;
import fr.milekat.infra.workers.lobby.commands.OpenLobbyMainGui;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class WorkerManager {
    public WorkerManager(JavaPlugin plugin) {
        if (Main.SERVER_TYPE.equals(ServerType.HOST)) {
            try {
                Main.HOST_INSTANCE = Main.getStorage().getInstance(Main.INSTANCE_ID);
                Bukkit.getPluginManager().registerEvents(new GameState(Main.HOST_INSTANCE), Main.getInstance());
                Bukkit.getPluginManager().registerEvents(new GameEvents(), Main.getInstance());
                plugin.getCommand("host").setExecutor(new OpenHostMainGui());
                if (Main.HOST_INSTANCE!=null) {
                    Main.HOST_INSTANCE.setState(InstanceState.READY);
                    Main.getStorage().updateInstanceState(Main.HOST_INSTANCE);
                    if (Main.getMessaging().isActivate()) {
                        MessageToProxy.notifyGameReady();
                    }
                    Main.getOwnLogger().info("Server Ready.");
                } else {
                    Main.getOwnLogger().warning("Host instance not found");
                }
            } catch (StorageExecuteException exception) {
                Main.getOwnLogger().warning("Error while trying to load workers");
            } catch (MessagingSendException exception) {
                Main.getOwnLogger().warning("Server ready with a messaging error");
            }
        } else if (Main.SERVER_TYPE.equals(ServerType.LOBBY)) {
            plugin.getCommand("host").setExecutor(new OpenLobbyMainGui());
        }
    }
}
