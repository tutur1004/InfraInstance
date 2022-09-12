package fr.milekat.infra.workers.host.players;

import fr.milekat.infra.Main;
import fr.milekat.infra.messaging.exeptions.MessagingSendException;
import fr.milekat.infra.workers.host.messaging.HostProxySend;

import java.util.UUID;

public class PlayersList {
    public static void addPlayerToWhiteList(UUID uuid, String username) {
        try {
            HostProxySend.notifyInvitePlayer(Main.HOST_PLAYER, username);
            Main.WAIT_LIST.remove(uuid);
            Main.WHITE_LIST.put(uuid, username);
        } catch (MessagingSendException exception) {
            exception.printStackTrace();
        }
    }

    // TODO: 10/09/2022 Implement it ? :D
    public static void removePlayerFromWhiteList(UUID uuid) {
        // TODO: 10/09/2022 Kick player ?
        Main.WHITE_LIST.remove(uuid);
    }

    public static void removePlayerFromWaitList(UUID uuid) {
        try {
            HostProxySend.notifyHostDeniedRequest(Main.HOST_PLAYER, uuid);
            Main.WAIT_LIST.remove(uuid);
        } catch (MessagingSendException exception) {
            exception.printStackTrace();
        }
    }
}
