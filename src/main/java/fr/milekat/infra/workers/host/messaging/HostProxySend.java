package fr.milekat.infra.workers.host.messaging;

import fr.milekat.infra.Main;
import fr.milekat.infra.messaging.Messaging;
import fr.milekat.infra.messaging.MessagingCase;
import fr.milekat.infra.messaging.exeptions.MessagingSendException;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.UUID;

public class HostProxySend {
    /**
     * Notify proxy: Host player has joined this host game
     */
    public static void notifyHostJoined(@NotNull Player hostPlayer) throws MessagingSendException {
        Main.getMessaging().sendProxyMessage(hostPlayer, Messaging.TARGET_TO_PROXY,
                MessagingCase.HOST_JOINED, new ArrayList<>());
    }

    /**
     * Notify proxy: Host player has invited a player to join (The player should be connected to a lobby)
     */
    public static void notifyInvitePlayer(@NotNull Player hostPlayer, String playerName) throws MessagingSendException {
        Main.getMessaging().sendProxyMessage(hostPlayer, Messaging.TARGET_TO_PROXY,
                MessagingCase.HOST_INVITE_PLAYER, Collections.singletonList(playerName));
    }

    /**
     * Notify proxy: Host player has invited a player to join (The player should be connected to a lobby)
     */
    public static void notifyHostDeniedRequest(@NotNull Player hostPlayer, UUID deniedUuid) throws MessagingSendException {
        Main.getMessaging().sendProxyMessage(hostPlayer, Messaging.TARGET_TO_PROXY,
                MessagingCase.HOST_DENIED_REQUEST, Collections.singletonList(deniedUuid.toString()));
    }

    public static void notifyGameReady() throws MessagingSendException {
        Main.getMessaging().sendProxyMessage(null, Messaging.TARGET_TO_PROXY,
                MessagingCase.GAME_READY, new ArrayList<>());
    }

    public static void notifyGameFinish() throws MessagingSendException {
        Main.getMessaging().sendProxyMessage(null, Messaging.TARGET_TO_PROXY,
                MessagingCase.GAME_READY, new ArrayList<>());
    }
}
