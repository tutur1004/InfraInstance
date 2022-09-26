package fr.milekat.infra.workers.utils;

import fr.milekat.infra.api.classes.Instance;
import fr.milekat.infra.messaging.exeptions.MessagingSendException;
import fr.milekat.infra.messaging.sending.MessageToHost;
import fr.milekat.infra.messaging.sending.MessageToProxy;

import java.util.UUID;

public class JoinHandler {
    public static void serverClick(Instance instance, UUID uuid, String playerName)
            throws MessagingSendException {
        if (instance.getUser().getUuid().equals(uuid)) {
            MessageToProxy.notifyHostRejoin(uuid, instance.getName());
        } else {
            // TODO: 27/09/2022 cases: Host is open and not full
            // TODO: 27/09/2022 cases: Host is open but full
            // TODO: 27/09/2022 cases: Host is waiting list - Done - TO TEST
            MessageToHost.notifyJoinRequest(uuid, playerName, instance.getName());
        }
    }
}
