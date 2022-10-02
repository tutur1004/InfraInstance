package fr.milekat.infra.messaging;

import fr.milekat.infra.Main;
import fr.milekat.infra.api.classes.ServerType;
import fr.milekat.infra.messaging.exeptions.MessagingSendException;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Locale;

/**
 * <p>Messages semantic:</p>
 * <p>0. {@link MessagingImplementation#getServerIdentifier()}
 * <br>1. {@link MessageCase}
 * <br>2.[...] Message arguments</p>
 * */
public interface MessagingImplementation {
    //  Global settings
    String SEPARATOR = ".";
    String PREFIX = Main.getFileConfig().getString("messaging.prefix");

    //  RabbitMQ settings
    String RABBIT_EXCHANGE_TYPE = "x-rtopic";
    String RABBIT_EXCHANGE = PREFIX + RABBIT_EXCHANGE_TYPE + SEPARATOR + "exchange";
    String RABBIT_QUEUE = PREFIX + "queue" + SEPARATOR + getServerIdentifier();
    String RABBIT_ROUTING_KEY = PREFIX + getServerIdentifier();
    String RABBIT_TO_ALL_PROXY = PREFIX + Main.PROXY_PREFIX + SEPARATOR + "#";

    /**
     * Simple shortcut to get the server identifier
     */
    static @NotNull String getServerIdentifier() {
        if (Main.getMessaging().isActivate()) {
            if (Main.SERVER_TYPE.equals(ServerType.LOBBY)) {
                return Main.LOBBY_PREFIX + SEPARATOR + Bukkit.getPort();
            } else if (Main.SERVER_TYPE.equals(ServerType.HOST)) {
                return Main.SERVER_NAME.toLowerCase(Locale.ROOT).replaceAll("-", ".");
            } else return "unknown";
        } else return "none";
    }

    default boolean isActivate() {
        return false;
    }

    /**
     * Check if the provider is reachable
     * @return true if message sent successfully
     * @throws MessagingSendException triggered if send failed
     */
    default boolean checkSending() throws MessagingSendException {
        return false;
    }

    /**
     * Send a message through messaging provider
     *
     * @param target  Targeted channel
     * @param mCase   Type of message
     * @param message to send
     */
    default void sendMessage(String target, MessageCase mCase, List<String> message)
            throws MessagingSendException {
        if (Main.DEBUG) Main.getOwnLogger().warning("Messaging is disable");
    }

    /**
     * Disconnect from the message provider
     */
    default void disconnect() {
        if (Main.DEBUG) Main.getOwnLogger().info("Messaging disconnected");
    }
}
