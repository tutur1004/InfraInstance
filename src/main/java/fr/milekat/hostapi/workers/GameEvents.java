package fr.milekat.hostapi.workers;

import fr.milekat.hostapi.api.API;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;

public class GameEvents implements Listener {
    @EventHandler
    public void onHostJoin(@NotNull PlayerJoinEvent event) {
        if (event.getPlayer().getUniqueId().equals(API.getHost())) {
            HostProxySend.notifyHostJoined(event.getPlayer());
        }
    }
}
