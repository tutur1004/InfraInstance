package fr.milekat.infra.workers.host.listeners;

import fr.milekat.infra.Main;
import fr.milekat.infra.api.classes.Instance;
import fr.milekat.infra.api.classes.InstanceState;
import fr.milekat.infra.api.events.GameFinishedEvent;
import fr.milekat.infra.api.events.GameStartEvent;
import fr.milekat.infra.messaging.exeptions.MessagingSendException;
import fr.milekat.infra.storage.exeptions.StorageExecuteException;
import fr.milekat.infra.workers.host.messaging.HostProxySend;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginEnableEvent;

/**
 * Update storage instance states values
 */
public class GameState implements Listener {
    private final Instance thisHost;

    public GameState(Instance thisHost) throws StorageExecuteException {
        this.thisHost = thisHost;
    }

    @EventHandler
    public void onGameReady(PluginEnableEvent event) throws StorageExecuteException, MessagingSendException {
        if (thisHost!=null) {
            thisHost.setState(InstanceState.READY);
            Main.getStorage().updateInstance(thisHost);
            HostProxySend.notifyGameReady();
        } else {
            Main.getOwnLogger().warning("Host instance not found");
        }
    }

    @EventHandler
    public void onGameStart(GameStartEvent event) throws StorageExecuteException {
        if (thisHost!=null) {
            thisHost.setState(InstanceState.IN_PROGRESS);
            Main.getStorage().updateInstance(thisHost);
        } else {
            Main.getOwnLogger().warning("Host instance not found");
        }
    }

    @EventHandler
    public void onGameFinish(GameFinishedEvent event) throws StorageExecuteException, MessagingSendException {
        if (thisHost!=null) {
            thisHost.setState(InstanceState.ENDING);
            Main.getStorage().updateInstance(thisHost);
            HostProxySend.notifyGameFinish();
        } else {
            Main.getOwnLogger().warning("Host instance not found");
        }
    }
}
