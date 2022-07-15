package fr.milekat.hostapi.api;

import fr.milekat.hostapi.api.classes.ServerType;
import fr.milekat.hostapi.api.events.GameFinishedEvent;
import fr.milekat.hostapi.api.events.GameStartEvent;
import org.jetbrains.annotations.Nullable;
import fr.milekat.hostapi.Main;
import org.bukkit.Bukkit;

import java.util.UUID;

@SuppressWarnings("unused")
public class API {

    /**
     * Method to get the Minecraft {@link UUID} of the player who own this hosted game.
     * If this game is not a hosted game or {@link ServerType#LOBBY}, return null
     *
     * @return Minecraft {@link UUID} of player (If this game is a host, null otherwise)
     */
    @Nullable
    public static UUID getHost() {
        if (Main.SERVER_TYPE.equals(ServerType.HOST) || System.getenv().containsKey(Main.HOST_UUID_ENV_VAR_NAME)) {
            return UUID.fromString(System.getenv(Main.HOST_UUID_ENV_VAR_NAME));
        } else {
            return null;
        }
    }

    /**
     * Wrapper to trigger {@link GameStartEvent}, ignored if {@link ServerType#LOBBY}
     */
    public static void gameStart() {
        if (Main.SERVER_TYPE.equals(ServerType.LOBBY)) return;
        Bukkit.getPluginManager().callEvent(new GameStartEvent());
    }

    /**
     * Wrapper to trigger {@link GameFinishedEvent}, ignored if {@link ServerType#LOBBY}
     */
    public static void gameFinished() {
        if (Main.SERVER_TYPE.equals(ServerType.LOBBY)) return;
        Bukkit.getPluginManager().callEvent(new GameFinishedEvent());
    }
}
