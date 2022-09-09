package fr.milekat.hostapi.messaging;

import fr.milekat.hostapi.Main;
import fr.milekat.hostapi.messaging.adapter.minecraft.ReceivePluginMessage;
import fr.milekat.hostapi.messaging.adapter.minecraft.SendPluginMessage;
import fr.milekat.hostapi.messaging.adapter.rabbitmq.ReceiveRabbitMessage;
import fr.milekat.hostapi.messaging.adapter.rabbitmq.SendRabbitMessage;
import fr.milekat.hostapi.messaging.adapter.redis.SendRedisMessage;
import fr.milekat.hostapi.messaging.exeptions.MessagingLoaderException;
import fr.milekat.hostapi.messaging.exeptions.MessagingSendException;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;

public class MessagingManager {
    private final Messaging messaging;

    public MessagingManager(@NotNull FileConfiguration config) throws MessagingLoaderException {
        try {
            String messagingProvider = config.getString("storage.type");
            if (Main.DEBUG) {
                Main.getHostLogger().info("Loading messaging type: " + messagingProvider);
            }
            if (messagingProvider.equalsIgnoreCase("none")) {
                messaging = null;
                return;
            } else if (messagingProvider.equalsIgnoreCase("plugin")) {
                Bukkit.getServer().getMessenger().registerIncomingPluginChannel(Main.getInstance(),
                        Main.MESSAGE_CHANNEL, new ReceivePluginMessage());
                messaging = new SendPluginMessage();
            } else if (messagingProvider.equalsIgnoreCase("rabbitmq")) {
                new ReceiveRabbitMessage();
                messaging = new SendRabbitMessage();
            } else if (messagingProvider.equalsIgnoreCase("redis")) {
                messaging = new SendRedisMessage();
            } else {
                throw new MessagingLoaderException("Unsupported messaging type");
            }
            if (messaging.checkSending()) {
                if (Main.DEBUG) {
                    Main.getHostLogger().info("Messaging loaded");
                }
            } else {
                throw new MessagingLoaderException("Messaging are not loaded properly");
            }
        } catch (MessagingSendException exception) {
            throw new MessagingLoaderException("Can't load messaging properly");
        }
    }

    public Messaging getMessaging() {
        return this.messaging;
    }
}
