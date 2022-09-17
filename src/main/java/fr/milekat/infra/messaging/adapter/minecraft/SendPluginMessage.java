package fr.milekat.infra.messaging.adapter.minecraft;

import fr.milekat.infra.messaging.MessageCase;
import fr.milekat.infra.messaging.Messaging;
import fr.milekat.infra.messaging.exeptions.MessagingSendException;

import java.util.List;

public class SendPluginMessage implements Messaging {
    @Override
    public boolean checkSending() {
        return false;
    }

    @Override
    public void disconnect() {
        // TODO: 14/09/2022 todo :)
    }

    /**
     * Send a message to the proxy server
     *
     * @param target  Targeted channel (MainChannel for PluginMessage)
     * @param mCase   Type of message
     * @param message to send
     */
    @Override
    public void sendMessage(String target, MessageCase mCase, List<String> message)
            throws MessagingSendException {
        // TODO: 14/09/2022 todo :)
    }
}
