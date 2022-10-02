package fr.milekat.infra.messaging.processing;

import fr.milekat.infra.Main;
import fr.milekat.infra.api.classes.ServerType;
import fr.milekat.infra.messaging.MessageCase;
import fr.milekat.infra.messaging.MessagingImplementation;

import java.util.List;
import java.util.UUID;

/**
 * <p>Server receive a message from Host server</p>
 * <p>Messages semantic:</p>
 * <p>0. {@link MessagingImplementation#getServerIdentifier()}
 * <br>1. {@link MessageCase}</p>
 * <p>{@link MessageCase#JOIN_REQUEST} Proxy has handle the request
 * <br>2. {@link UUID} uuid of player who requesting
 * <br>3. {@link String} name of player who requesting</p>
 */
public class MessageFromLobby {
    public MessageFromLobby(List<String> message) {
        //  This case will never exist !
        String source = message.get(0);
        String server = source.replaceAll(MessagingImplementation.PREFIX, "")
                .replaceAll("\\.", "-");
        MessageCase mCase = MessageCase.valueOf(message.get(1));
        switch (mCase) {
            case JOIN_REQUEST: {
                if (Main.SERVER_TYPE.equals(ServerType.LOBBY)) {
                    if (message.size()==4) {
                        try {
                            UUID uuid = UUID.fromString(message.get(2));
                            String name = message.get(3);
                            Main.WAIT_LIST.put(uuid, name);
                        } catch (IllegalArgumentException exception) {
                            if (Main.DEBUG) {
                                Main.getOwnLogger().warning("Wrong UUID type ? " + message);
                            }
                        }
                    }
                }
                break;
            }
            default: {
                if (Main.DEBUG) {
                    Main.getOwnLogger().warning("Receive message from lobby with unknown case, " +
                            "message: " + message);
                }
            }
        }
    }
}
