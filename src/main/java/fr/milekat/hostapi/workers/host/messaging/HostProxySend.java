package fr.milekat.hostapi.workers.host.messaging;

import fr.milekat.hostapi.Main;
import fr.milekat.hostapi.messaging.MessagingCase;
import fr.milekat.hostapi.messaging.exeptions.MessagingSendException;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;

public class HostProxySend {
    /**
     * Notify proxy: Host player has joined this host game
     */
    public static void notifyHostJoined(@NotNull Player hostPlayer) throws MessagingSendException {
        Main.getMessaging().sendProxyMessage(hostPlayer, MessagingCase.HOST_JOINED, new ArrayList<>());
    }

    /**
     * Notify proxy: Host player has invited a player to join (The player should be connected to a lobby)
     */
    public static void notifyInvitePlayer(@NotNull Player hostPlayer, String playerName) throws MessagingSendException {
        Main.getMessaging().sendProxyMessage(hostPlayer, MessagingCase.HOST_INVITE_PLAYER,
                Collections.singletonList(playerName));
    }
}
