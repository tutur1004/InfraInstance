package fr.milekat.infra.messaging;

import fr.milekat.infra.Main;
import fr.milekat.infra.api.classes.ServerType;
import fr.milekat.infra.messaging.exeptions.MessagingSendException;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Locale;

public interface Messaging {
    //  Global settings
    String TARGET_TO_PROXY = "proxy";
    String TARGET_TO_LOBBY_PREFIX = "lobby";
    String TARGET_TO_HOST_PREFIX = "host";

    //  PluginMessage settings
    String MESSAGE_CHANNEL = "INFRA_MESSAGING";

    //  RabbitMQ settings
    String RABBIT_SEPARATOR = ".";
    String RABBIT_PREFIX = Main.getFileConfig().getString("messaging.rabbit-mq.prefix");
    String RABBIT_EXCHANGE = RABBIT_PREFIX + "topic" + RABBIT_SEPARATOR + "exchange";
    String RABBIT_QUEUE = RABBIT_PREFIX + "queue" + RABBIT_SEPARATOR + getServerIdentifier();
    String RABBIT_ROUTING_KEY = RABBIT_PREFIX + getServerIdentifier();

    /**
     * Simple shortcut to get the server identifier
     */
    static @NotNull String getServerIdentifier() {
        if (Main.SERVER_TYPE.equals(ServerType.LOBBY)) {
            return String.valueOf(Bukkit.getPort());
        } else if (Main.SERVER_TYPE.equals(ServerType.HOST)) {
            return Main.SERVER_NAME.toLowerCase(Locale.ROOT).replaceAll("-", ".");
        } else return "";
    }

    /**
     * Check if the provider is reachable
     * @return true if message sent successfully
     * @throws MessagingSendException triggered if send failed
     */
    boolean checkSending() throws MessagingSendException;

    /**
     * Send a message to the proxy server
     *
     * @param player  source player
     * @param target  Targeted channel (MainChannel for PluginMessage, RoutingKey for RabbitMQ)
     * @param mCase   Type of message
     * @param message to send
     */
    void sendProxyMessage(Player player, String target, MessagingCase mCase, List<String> message)
            throws MessagingSendException;

    /**
     * Disconnect from the message provider
     */
    void disconnect();
}
