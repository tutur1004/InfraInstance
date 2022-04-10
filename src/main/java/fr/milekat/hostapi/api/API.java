package fr.milekat.hostapi.api;

import org.jetbrains.annotations.Nullable;
import fr.milekat.hostapi.HostAPI;
import org.bukkit.Bukkit;

import java.util.UUID;

public class API {

    /**
     * Method to get the Minecraft {@link UUID} of the player who own this hosted game.
     * If this game is not a hosted game, return null
     *
     * @return Minecraft {@link UUID} of player (If this game is a host, null otherwise)
     */
    @Nullable
    public static UUID getHost() {
        if (System.getenv().containsKey(HostAPI.HOST_UUID_ENV_VAR_NAME)) {
            return UUID.fromString(System.getenv(HostAPI.HOST_UUID_ENV_VAR_NAME));
        } else {
            return null;
        }
    }

    /**
     * Wrapper to trigger {@link GameStartEvent}
     */
    public static void gameStart() {
        Bukkit.getPluginManager().callEvent(new GameStartEvent());
    }

    /**
     * Wrapper to trigger {@link GameFinishedEvent}
     */
    public static void gameFinished() {
        Bukkit.getPluginManager().callEvent(new GameFinishedEvent());
    }
}
