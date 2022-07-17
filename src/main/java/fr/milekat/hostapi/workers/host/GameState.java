package fr.milekat.hostapi.workers.host;

import fr.milekat.hostapi.Main;
import fr.milekat.hostapi.api.classes.Instance;
import fr.milekat.hostapi.api.classes.InstanceState;
import fr.milekat.hostapi.api.events.GameFinishedEvent;
import fr.milekat.hostapi.api.events.GameStartEvent;
import fr.milekat.hostapi.storage.exeptions.StorageExecuteException;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginEnableEvent;

/**
 * Update storage instance states values
 */
public class GameState implements Listener {
    @EventHandler
    public void onGameReady(PluginEnableEvent event) throws StorageExecuteException {
        Instance instance = Main.getStorage().getInstance(Main.SERVER_NAME);
        if (instance!=null) {
            instance.setState(InstanceState.READY);
            Main.getStorage().updateInstance(instance);
        } else {
            Main.getHostLogger().warning("Host instance not found");
        }
    }

    @EventHandler
    public void onGameStart(GameStartEvent event) throws StorageExecuteException {
        Instance instance = Main.getStorage().getInstance(Main.SERVER_NAME);
        if (instance!=null) {
            instance.setState(InstanceState.IN_PROGRESS);
            Main.getStorage().updateInstance(instance);
        } else {
            Main.getHostLogger().warning("Host instance not found");
        }
    }

    @EventHandler
    public void onGameFinish(GameFinishedEvent event) throws StorageExecuteException {
        Instance instance = Main.getStorage().getInstance(Main.SERVER_NAME);
        if (instance!=null) {
            instance.setState(InstanceState.ENDING);
            Main.getStorage().updateInstance(instance);
        } else {
            Main.getHostLogger().warning("Host instance not found");
        }
    }
}
