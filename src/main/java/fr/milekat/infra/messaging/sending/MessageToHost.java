package fr.milekat.infra.messaging.sending;

import fr.milekat.infra.Main;
import fr.milekat.infra.messaging.MessageCase;
import fr.milekat.infra.messaging.exeptions.MessagingSendException;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.UUID;

public class MessageToHost {
    /**
     * Notify host: A player in lobby want join the game
     */
    public static void notifyJoinRequest(@NotNull UUID uuid, @NotNull String playerName, String host)
            throws MessagingSendException {
        Main.getMessaging().sendMessage(host, MessageCase.JOIN_REQUEST, Arrays.asList(uuid.toString(), playerName));
    }
}
