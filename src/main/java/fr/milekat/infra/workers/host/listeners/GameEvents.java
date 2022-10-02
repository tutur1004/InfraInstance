package fr.milekat.infra.workers.host.listeners;

import fr.milekat.infra.Main;
import fr.milekat.infra.api.API;
import fr.milekat.infra.api.events.GameStartEvent;
import fr.milekat.infra.messaging.exeptions.MessagingSendException;
import fr.milekat.infra.messaging.sending.MessageToProxy;
import fr.milekat.infra.storage.exeptions.StorageExecuteException;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;

public class GameEvents implements Listener {
    @EventHandler
    public void onHostJoin(@NotNull PlayerJoinEvent event) throws MessagingSendException {
        if (event.getPlayer().getUniqueId().equals(API.getHost())) {
            Main.HOST_PLAYER = event.getPlayer();
            MessageToProxy.notifyHostJoined();
        }
    }

    /**
     * Consume host ticket, if this host is not free (Admin created)
     */
    @EventHandler (priority = EventPriority.LOWEST)
    public void onGameStart(@NotNull GameStartEvent event) throws StorageExecuteException {
        if (!event.isCancelled()) {
            if (!System.getenv().containsKey(Main.HOST_FREE_ENV_VAR_NAME) ||
                    !System.getenv().get(Main.HOST_FREE_ENV_VAR_NAME).equalsIgnoreCase("true")) {
                Main.getStorage().removePlayerTickets(API.getHost(), 1);
            }
        }
    }
}
