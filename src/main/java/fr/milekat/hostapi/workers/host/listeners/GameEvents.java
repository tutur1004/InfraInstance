package fr.milekat.hostapi.workers.host.listeners;

import fr.milekat.hostapi.Main;
import fr.milekat.hostapi.api.API;
import fr.milekat.hostapi.messaging.exeptions.MessagingSendException;
import fr.milekat.hostapi.workers.host.messaging.HostProxySend;
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
