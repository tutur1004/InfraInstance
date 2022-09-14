package fr.milekat.infra.workers.host.messaging;

import fr.milekat.infra.Main;
import fr.milekat.infra.messaging.Messaging;
import fr.milekat.infra.messaging.MessagingCase;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

/**
 * <p>Host server receive a message from Proxy server</p>
 * <p>Messages semantic:</p>
 * <p>0. {@link Messaging#getServerIdentifier()}
 * <br>1. {@link MessagingCase}</p>
 * <p>{@link MessagingCase#INVITE_SENT} Proxy has handle the request
 * <br>2. {@link Player} invited</p>
 * <p>{@link MessagingCase#INVITE_RESULT_NOT_FOUND} Target player net found
 * <br>2. {@link Player} invited</p>
 * <p>{@link MessagingCase#INVITE_RESULT_DENY} Target player denied the invitation
 * <br>2. {@link Player} invited</p>
 * <p>{@link MessagingCase#JOIN_REQUEST} A player ask to join the host (Without invitation while access is off)
 * <br>2. The {@link UUID} of the player who's asking
 * <br>3. The {@link Player#getName()} of the player who's asking</p>
 * */
public class HostProxyReceive {
    public HostProxyReceive(List<String> message) {
        MessagingCase mCase = MessagingCase.valueOf(message.get(0));
        switch (mCase) {
            case INVITE_SENT: {
                if (message.size()==3) {
                    String invited = message.get(2);
                    Main.HOST_PLAYER.sendMessage("Invitation to " + invited + " sent !");
                    break;
                }
            }
            case INVITE_RESULT_NOT_FOUND: {
                if (message.size()==3) {
                    String invited = message.get(2);
                    Main.HOST_PLAYER.sendMessage("§6Player '" + invited + "' not found in lobby.");
                    break;
                }
            }
            case INVITE_RESULT_DENY: {
                if (message.size()==3) {
                    String invited = message.get(2);
                    Main.HOST_PLAYER.sendMessage("§6Player '" + invited + "' has denied your request.");
                }
                break;
            }
            case JOIN_REQUEST: {
                if (message.size()==4) {
                    Main.WAIT_LIST.put(UUID.fromString(message.get(2)), message.get(3));
                }
            }
        }
    }
}
