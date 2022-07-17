package fr.milekat.hostapi.workers.host;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import fr.milekat.hostapi.Main;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class HostProxySend {
    private static final String SUB_CHANNEL = Main.SERVER_ID;

    private static final String HOST_JOINED = "HOST_JOINED";
    private static final String INVITE_PLAYER = "INVITE_PLAYER";

    /**
     * Notify proxy: Host player has joined this host game
     */
    public static void notifyHostJoined(@NotNull Player hostPlayer) {
        @SuppressWarnings("UnstableApiUsage") ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF(SUB_CHANNEL);
        out.writeUTF(HOST_JOINED);
        hostPlayer.sendPluginMessage(Main.getInstance(), Main.MESSAGE_CHANNEL, out.toByteArray());
    }

    /**
     * Notify proxy: Host player has invited a player to join (The player should be connected to a lobby)
     */
    public static void notifyInvitePlayer(@NotNull Player hostPlayer, String playerName) {
        @SuppressWarnings("UnstableApiUsage") ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF(SUB_CHANNEL);
        out.writeUTF(INVITE_PLAYER);
        out.writeUTF(playerName);
        hostPlayer.sendPluginMessage(Main.getInstance(), Main.MESSAGE_CHANNEL, out.toByteArray());
    }
}
