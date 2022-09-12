package fr.milekat.infra.messaging;

import fr.milekat.infra.Main;
import fr.milekat.infra.messaging.adapter.minecraft.ReceivePluginMessage;
import fr.milekat.infra.messaging.adapter.minecraft.SendPluginMessage;
import fr.milekat.infra.messaging.adapter.rabbitmq.ReceiveRabbitMessage;
import fr.milekat.infra.messaging.adapter.rabbitmq.SendRabbitMessage;
import fr.milekat.infra.messaging.adapter.redis.SendRedisMessage;
import fr.milekat.infra.messaging.exeptions.MessagingLoaderException;
import fr.milekat.infra.messaging.exeptions.MessagingSendException;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;

public class MessagingManager {
    private final Messaging messaging;

    public MessagingManager(@NotNull FileConfiguration config) throws MessagingLoaderException {
        try {
            String messagingProvider = config.getString("messaging.type");
            if (Main.DEBUG) {
                Main.getOwnLogger().info("Loading messaging type: " + messagingProvider);
            }
            if (messagingProvider.equalsIgnoreCase("none")) {
                messaging = null;
                return;
            } else if (messagingProvider.equalsIgnoreCase("plugin")) {
                Bukkit.getServer().getMessenger().registerIncomingPluginChannel(Main.getInstance(),
                        Messaging.MESSAGE_CHANNEL, new ReceivePluginMessage());
                messaging = new SendPluginMessage();
            } else if (messagingProvider.equalsIgnoreCase("rabbitmq")) {
                new ReceiveRabbitMessage().getRabbitConsumerThread().start();
                messaging = new SendRabbitMessage();
            } else if (messagingProvider.equalsIgnoreCase("redis")) {
                messaging = new SendRedisMessage();
            } else {
                throw new MessagingLoaderException("Unsupported messaging type");
            }
            if (messaging.checkSending()) {
                if (Main.DEBUG) {
                    Main.getOwnLogger().info("Messaging loaded");
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
