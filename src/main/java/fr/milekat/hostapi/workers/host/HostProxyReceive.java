package fr.milekat.hostapi.workers.host;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import fr.milekat.hostapi.Main;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

public class HostProxyReceive implements PluginMessageListener {
    private static final String SUB_CHANNEL = Main.SERVER_ID;

    private static final String INVITE_SENT = "INVITE_SENT";
    private static final String INVITE_NOT_FOUND = "INVITE_NOT_FOUND";
    private static final String INVITE_DENY = "INVITE_DENY";

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        @SuppressWarnings("UnstableApiUsage") ByteArrayDataInput in = ByteStreams.newDataInput(message);
        String subChannel = in.readUTF();
        if (subChannel.equalsIgnoreCase(SUB_CHANNEL)) {
            String action = in.readUTF();
            switch (action) {
                case INVITE_SENT:
                    player.sendMessage("Player: " + in.readUTF() + " invited");
                    break;
                case INVITE_NOT_FOUND:
                    player.sendMessage("Player: " + in.readUTF() + " not found in lobby");
                    break;
                case INVITE_DENY:
                    player.sendMessage("Player: " + in.readUTF() + " has denied your invitation");
                    break;
            }
        }
    }
}
