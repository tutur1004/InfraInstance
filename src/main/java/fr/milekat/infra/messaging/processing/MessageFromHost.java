package fr.milekat.infra.messaging.processing;

import fr.milekat.infra.Main;
import fr.milekat.infra.messaging.MessageCase;
import fr.milekat.infra.messaging.Messaging;

import java.util.List;

public class MessageFromHost {
    public MessageFromHost(List<String> message) {
        // TODO: 14/09/2022 Process message from host
        String source = message.get(0);
        String server = source.replaceAll(Messaging.PREFIX, "")
                .replaceAll("\\.", "-");
        MessageCase mCase = MessageCase.valueOf(message.get(1));
        switch (mCase) {
            default: {
                if (Main.DEBUG) {
                    Main.getOwnLogger().warning("Receive message from lobby with unknown case, " +
                            "message: " + message);
                }
            }
        }
    }
}
