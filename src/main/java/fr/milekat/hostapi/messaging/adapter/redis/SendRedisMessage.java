package fr.milekat.hostapi.messaging.adapter.redis;

import fr.milekat.hostapi.messaging.Messaging;
import fr.milekat.hostapi.messaging.MessagingCase;
import fr.milekat.hostapi.messaging.exeptions.MessagingSendException;
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
     * @param message to send
     */
    @Override
    public void sendProxyMessage(Player player, MessagingCase mCase, List<String> message) {

    }
}
