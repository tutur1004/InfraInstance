package fr.milekat.infra.api;

import fr.milekat.infra.Main;
import fr.milekat.infra.api.classes.Instance;
import fr.milekat.infra.api.classes.ServerType;
import fr.milekat.infra.api.classes.User;
import fr.milekat.infra.api.events.GameFinishedEvent;
import fr.milekat.infra.api.events.GameStartEvent;
import fr.milekat.infra.storage.StorageImplementation;
import fr.milekat.infra.storage.exeptions.StorageExecuteException;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

@SuppressWarnings("unused")
public class API {
    private static final StorageImplementation STORAGE = Main.getStorage();

    /*
        This host
     */
    /**
     * Method to get the Minecraft {@link UUID} of the player who own this hosted game.
     * If this server is not a {@link ServerType#HOST}, return null
     *
     * @return Minecraft {@link UUID} of {@link Player}
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
     * Get the server type of this current server.
     * useful to check if this server is a {@link ServerType#HOST} before trying to get something
     */
    @NotNull
    public static ServerType getServerType() {
        return Main.SERVER_TYPE;
    }

    /**
     * Method to get the {@link Instance} of this host.
     * If this server is not a {@link ServerType#HOST}, return null
     *
     * @return {@link Instance}
     */
    @Nullable
    public static Instance getHostInstance() throws StorageExecuteException {
        if (Main.SERVER_TYPE.equals(ServerType.HOST)) {
            return getInstance(Main.SERVER_NAME);
        } else {
            return null;
        }
    }

    /**
     * Get the host message of this host, return null if it's not a {@link ServerType#HOST}
     * @return instance message {@link String}
     */
    @Nullable
    public static String getHostMessage() throws StorageExecuteException {
        if (Main.SERVER_TYPE.equals(ServerType.HOST)) {
            Instance instance = getHostInstance();
            if (instance==null) return null;
            return getHostInstance().getMessage();
        } else {
            return null;
        }
    }

    /**
     * Update the host message of this host, ignored if it's not a {@link ServerType#HOST}
     * @param message new {@link String} message for this host
     */
    public static void updateHostMessage(@NotNull String message) throws StorageExecuteException {
        if (Main.SERVER_TYPE.equals(ServerType.HOST)) {
            Instance instance = getHostInstance();
            if (instance!=null) {
                instance.setMessage(message);
                updateInstance(instance);
            }
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

    /*
        Global tickets
     */
    /**
     * Get tickets amount of player
     * @param uuid {@link UUID} of {@link Player}
     * @return ticket amount
     */
    public static Integer getTickets(UUID uuid) throws StorageExecuteException {
        return STORAGE.getTicket(uuid);
    }

    /**
     * Get tickets amount of player
     * @param player {@link Player}
     * @return ticket amount
     */
    public static Integer getTickets(@NotNull Player player) throws StorageExecuteException {
        return getTickets(player.getUniqueId());
    }

    /**
     * Add tickets to player
     * @param player {@link Player}
     */
    public static void addPlayerTickets(@NotNull Player player, Integer amount) throws StorageExecuteException {
        STORAGE.addPlayerTickets(player.getUniqueId(), amount);
    }

    /*
        Global Instance
     */
    public static Instance getInstance(String name) throws StorageExecuteException {
        return STORAGE.getInstance(name);
    }

    public static void updateInstance(Instance instance) throws StorageExecuteException {
        STORAGE.updateInstance(instance);
    }

    /*
        Global User
     */
    /**
     * Get a {@link User} if present, otherwise return null
     * @param uuid of player
     * @return {@link User} or null
     */
    @Nullable
    public static User getUser(UUID uuid) throws StorageExecuteException {
        return STORAGE.getUser(uuid);
    }
}
