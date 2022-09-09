package fr.milekat.hostapi.messaging;

import fr.milekat.hostapi.Main;
import fr.milekat.hostapi.api.classes.ServerType;
import fr.milekat.hostapi.messaging.exeptions.MessagingSendException;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface Messaging {
    String HOST_RABBIT_PREFIX = Main.getFileConfig().getString("messaging.rabbit-mq.prefix");
    String RABBIT_EXCHANGE = HOST_RABBIT_PREFIX + "EXCHANGE";
    String RABBIT_CONSUMER_ROUTING_KEY = HOST_RABBIT_PREFIX + "ROUTING_KEY_" + getIdentifier();
    String RABBIT_PUBLISHER_ROUTING_KEY = HOST_RABBIT_PREFIX + "ROUTING_KEY_" + getIdentifier();
    String RABBIT_CONSUMER_QUEUE = HOST_RABBIT_PREFIX + "CONSUMER_QUEUE_" + getIdentifier();
    String RABBIT_PUBLISHER_QUEUE = HOST_RABBIT_PREFIX + "PUBLISHER_QUEUE_" + getIdentifier();

    static @NotNull String getIdentifier() {
        if (Main.SERVER_TYPE.equals(ServerType.LOBBY)) {
            return "LOBBY_" + Bukkit.getPort();
        } else if (Main.SERVER_TYPE.equals(ServerType.HOST)) {
            return "HOST_" + Main.SERVER_NAME;
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
     * @param player source player
     * @param mCase Type of message
     * @param message to send
     */
    void sendProxyMessage(Player player, MessagingCase mCase, List<String> message) throws MessagingSendException;

    /**
     * Disconnect from the message provider
     */
    void disconnect();
}
