package fr.milekat.infra.messaging.adapter.redis;

import fr.milekat.infra.messaging.Messaging;
import fr.milekat.infra.messaging.MessagingCase;
import fr.milekat.infra.messaging.exeptions.MessagingSendException;
import org.bukkit.entity.Player;

import java.util.List;

public class SendRedisMessage implements Messaging {
    @Override
    public boolean checkSending() throws MessagingSendException {
        return false;
    }

    @Override
    public void disconnect() {

    }

    /**
     * Send a message to the proxy server
     *
     * @param p       source player
     * @param target  Targeted channel (RoutingKey for RabbitMQ)
     * @param mCase   Type of message
     * @param message to send
     */
    @Override
    public void sendMessage(Player p, String target, MessagingCase mCase, List<String> message) {

    }
}
