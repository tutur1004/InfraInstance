package fr.milekat.infra.messaging.processing;

import fr.milekat.infra.Main;
import fr.milekat.infra.api.classes.ServerType;

import java.util.List;

public class MessageFromHost {
    public MessageFromHost(List<String> message) {
        if (!Main.SERVER_TYPE.equals(ServerType.LOBBY)) return;
        // TODO: 14/09/2022 Process message from host
    }
}
