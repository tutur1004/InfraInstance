package fr.milekat.infra.messaging.adapter.minecraft;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import fr.milekat.infra.Main;
import fr.milekat.infra.api.classes.ServerType;
import fr.milekat.infra.messaging.Messaging;
import fr.milekat.infra.workers.host.messaging.HostProxyReceive;
import fr.milekat.infra.workers.lobby.LobbyProxyReceive;
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
            if (Main.SERVER_TYPE.equals(ServerType.LOBBY) && subChannel.startsWith(Messaging.TARGET_TO_LOBBY_PREFIX)) {
                //  Check if message is addressed to this lobby
                new LobbyProxyReceive(); // TODO: 08/09/2022 LobbyProxyReceive
            } else if (Main.SERVER_TYPE.equals(ServerType.HOST) && subChannel.equalsIgnoreCase(Main.SERVER_ID)) {
                //  Check if message is addressed to this host
                new HostProxyReceive(message);
            }
        }
    }
}
