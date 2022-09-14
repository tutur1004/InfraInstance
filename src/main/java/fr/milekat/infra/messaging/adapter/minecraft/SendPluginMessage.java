package fr.milekat.infra.messaging.adapter.minecraft;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import fr.milekat.infra.Main;
import fr.milekat.infra.messaging.Messaging;
import fr.milekat.infra.messaging.MessagingCase;
import fr.milekat.infra.messaging.exeptions.MessagingSendException;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class SendPluginMessage implements Messaging {
    List<MessagingCase> pmAllowedCases = Arrays.asList(MessagingCase.HOST_JOINED,
            MessagingCase.HOST_INVITE_PLAYER, MessagingCase.HOST_DENIED_REQUEST, MessagingCase.ASK_CREATE_HOST);

    @Override
    public boolean checkSending() {
        return false; // TODO: 14/09/2022 To ensure no one use this, TO REMOVE OR TO REWORK !
    }

    @Override
    public void disconnect() {
        Bukkit.getServer().getMessenger().unregisterOutgoingPluginChannel(Main.getInstance(),Messaging.MESSAGE_CHANNEL);
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
    public void sendMessage(Player p, String target, MessagingCase mCase, List<String> message)
            throws MessagingSendException {
        if (pmAllowedCases.contains(mCase)) {
            try {
                @SuppressWarnings("UnstableApiUsage") ByteArrayDataOutput out = ByteStreams.newDataOutput();
                out.writeUTF(target);
                out.writeUTF(mCase.name());
                message.forEach(out::writeUTF);
                p.sendPluginMessage(Main.getInstance(), Messaging.MESSAGE_CHANNEL, out.toByteArray());
            } catch (Exception exception) {
                throw new MessagingSendException(exception, "Error while trying to send message");
            }
        }
    }
}
