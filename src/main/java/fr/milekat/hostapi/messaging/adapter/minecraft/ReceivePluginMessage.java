package fr.milekat.hostapi.messaging.adapter.minecraft;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import fr.milekat.hostapi.Main;
import fr.milekat.hostapi.api.classes.ServerType;
import fr.milekat.hostapi.workers.host.messaging.HostProxyReceive;
import fr.milekat.hostapi.workers.lobby.LobbyProxyReceive;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.util.ArrayList;
import java.util.List;

public class ReceivePluginMessage implements PluginMessageListener {
    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] rawMessage) {
        @SuppressWarnings("UnstableApiUsage") ByteArrayDataInput in = ByteStreams.newDataInput(rawMessage);
        String mainChannel = in.readUTF();
        if (mainChannel.equalsIgnoreCase("proxy")) {
            String subChannel = in.readUTF();
            List<String> message = new ArrayList<>();
            String line = in.readLine();
            do {
                message.add(line);
                line = in.readLine();
            } while (line!=null);
            if (Main.SERVER_TYPE.equals(ServerType.LOBBY) && subChannel.equalsIgnoreCase(ServerType.LOBBY.name())) {
                //  Check if message is addressed to this lobby
                new LobbyProxyReceive(); // TODO: 08/09/2022 LobbyProxyReceive
            } else if (Main.SERVER_TYPE.equals(ServerType.HOST) && subChannel.equalsIgnoreCase(Main.SERVER_ID)) {
                //  Check if message is addressed to this host
                new HostProxyReceive(message);
            }
        }
    }
}
