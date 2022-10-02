package fr.milekat.infra.messaging;

import fr.milekat.infra.Main;
import fr.milekat.infra.messaging.adapter.rabbitmq.ReceiveRabbitMessage;
import fr.milekat.infra.messaging.adapter.rabbitmq.SendRabbitMessage;
import fr.milekat.infra.messaging.adapter.redis.SendRedisMessage;
import fr.milekat.infra.messaging.exeptions.MessagingLoaderException;
import fr.milekat.infra.messaging.exeptions.MessagingSendException;
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
                messaging = new DefaultMessaging();
                return;
            } else if (messagingProvider.equalsIgnoreCase("rabbitmq")) {
                new ReceiveRabbitMessage().getRabbitConsumerThread().start();
                messaging = new SendRabbitMessage();
            } else if (messagingProvider.equalsIgnoreCase("redis")) {
                // TODO: 02/10/2022 Redis consumer
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

    public static class DefaultMessaging implements Messaging {}
}
