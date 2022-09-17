package fr.milekat.infra.messaging.processing;

import fr.milekat.infra.Main;
import fr.milekat.infra.messaging.MessageCase;
import fr.milekat.infra.messaging.Messaging;

import java.util.List;

public class MessageFromLobby {
    public MessageFromLobby(List<String> message) {
        //  This case will never exist !
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
