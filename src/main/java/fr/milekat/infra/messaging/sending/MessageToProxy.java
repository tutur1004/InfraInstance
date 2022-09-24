package fr.milekat.infra.messaging.sending;

import fr.milekat.infra.Main;
import fr.milekat.infra.messaging.MessageCase;
import fr.milekat.infra.messaging.Messaging;
import fr.milekat.infra.messaging.exeptions.MessagingSendException;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.UUID;

public class MessageToProxy {
    /**
     * Notify proxy: Host player has joined this host game
     */
    public static void notifyHostJoined() throws MessagingSendException {
        Main.getMessaging().sendMessage(Messaging.RABBIT_TO_ALL_PROXY,
                MessageCase.HOST_JOINED, new ArrayList<>());
    }

    /**
     * Notify proxy: Host player has invited a player to join (The player should be connected to a lobby)
     */
    public static void notifyInvitePlayer(@NotNull String playerName) throws MessagingSendException {
        Main.getMessaging().sendMessage(Messaging.RABBIT_TO_ALL_PROXY,
                MessageCase.HOST_INVITE_PLAYER, Collections.singletonList(playerName));
    }

    /**
     * Notify proxy: Host player has invited a player to join (The player should be connected to a lobby)
     */
    public static void notifyHostDeniedRequest(@NotNull UUID deniedUuid) throws MessagingSendException {
        Main.getMessaging().sendMessage(Messaging.RABBIT_TO_ALL_PROXY,
                MessageCase.HOST_DENIED_REQUEST, Collections.singletonList(deniedUuid.toString()));
    }

    public static void notifyGameReady() throws MessagingSendException {
        Main.getMessaging().sendMessage(Messaging.RABBIT_TO_ALL_PROXY,
                MessageCase.GAME_READY, new ArrayList<>());
    }

    public static void notifyGameFinish() throws MessagingSendException {
        Main.getMessaging().sendMessage(Messaging.RABBIT_TO_ALL_PROXY,
                MessageCase.GAME_READY, new ArrayList<>());
    }

    /**
     * Notify proxy: Host player want to rejoin his host instance
     */
    public static void notifyHostRejoin(@NotNull UUID uuid, String host) throws MessagingSendException {
        Main.getMessaging().sendMessage(host, MessageCase.HOST_REJOIN, Collections.singletonList(uuid.toString()));
    }

    /**
     * Notify proxy: Host player want to rejoin his host instance
     */
    public static void notifyCreateHost(@NotNull UUID uuid, int id) throws MessagingSendException {
        Main.getMessaging().sendMessage(Messaging.RABBIT_TO_ALL_PROXY, MessageCase.ASK_CREATE_HOST,
                Collections.singletonList(String.valueOf(id)));
    }
}
