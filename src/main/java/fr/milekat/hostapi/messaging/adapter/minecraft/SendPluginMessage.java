package fr.milekat.hostapi.messaging.adapter.minecraft;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import fr.milekat.hostapi.Main;
import fr.milekat.hostapi.messaging.Messaging;
import fr.milekat.hostapi.messaging.MessagingCase;
import fr.milekat.hostapi.messaging.exeptions.MessagingSendException;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class SendPluginMessage implements Messaging {
    List<MessagingCase> pmAllowedCases = Arrays.asList(MessagingCase.HOST_JOINED,
            MessagingCase.HOST_INVITE_PLAYER, MessagingCase.HOST_DENIED_REQUEST, MessagingCase.ASK_CREATE_HOST);

    @Override
    public boolean checkSending() {
        return true;
    }

    @Override
    public void disconnect() {
        Bukkit.getServer().getMessenger().unregisterOutgoingPluginChannel(Main.getInstance(), Main.MESSAGE_CHANNEL);
    }

    /**
     * Send a message to the proxy server
     *
     * @param p       source player
     * @param target  Targeted channel (MainChannel for PluginMessage)
     * @param mCase   Type of message
     * @param message to send
     */
    @Override
    public void sendProxyMessage(Player p, String target, MessagingCase mCase, List<String> message)
            throws MessagingSendException {
        if (pmAllowedCases.contains(mCase)) {
            try {
                @SuppressWarnings("UnstableApiUsage") ByteArrayDataOutput out = ByteStreams.newDataOutput();
                out.writeUTF(target);
                out.writeUTF(mCase.name());
                message.forEach(out::writeUTF);
                p.sendPluginMessage(Main.getInstance(), Main.MESSAGE_CHANNEL, out.toByteArray());
            } catch (Exception exception) {
                throw new MessagingSendException(exception, "Error while trying to send message");
            }
        }
    }
}
