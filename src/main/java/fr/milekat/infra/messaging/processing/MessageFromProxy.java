package fr.milekat.infra.messaging.processing;

import fr.milekat.infra.Main;
import fr.milekat.infra.api.classes.ServerType;
import fr.milekat.infra.workers.host.messaging.HostProxyReceive;
import fr.milekat.infra.workers.lobby.LobbyProxyReceive;

import java.util.List;

public class MessageFromProxy {
    public MessageFromProxy(List<String> message) {
        if (Main.SERVER_TYPE.equals(ServerType.LOBBY)) {
            new LobbyProxyReceive(); // TODO: 08/09/2022 LobbyProxyReceive
        } else if (Main.SERVER_TYPE.equals(ServerType.HOST)) {
            new HostProxyReceive(message);
        }
    }
}
