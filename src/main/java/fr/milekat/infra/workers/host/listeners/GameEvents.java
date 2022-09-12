package fr.milekat.infra.workers.host.listeners;

import fr.milekat.infra.Main;
import fr.milekat.infra.api.API;
import fr.milekat.infra.messaging.exeptions.MessagingSendException;
import fr.milekat.infra.workers.host.messaging.HostProxySend;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;

public class GameEvents implements Listener {
    @EventHandler
    public void onHostJoin(@NotNull PlayerJoinEvent event) throws MessagingSendException {
        if (event.getPlayer().getUniqueId().equals(API.getHost())) {
            Main.HOST_PLAYER = event.getPlayer();
            HostProxySend.notifyHostJoined(event.getPlayer());
        }
    }
}
