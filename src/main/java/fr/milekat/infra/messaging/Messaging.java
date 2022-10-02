package fr.milekat.infra.messaging;

import fr.milekat.infra.Main;
import fr.milekat.infra.messaging.adapter.rabbitmq.ReceiveRabbitMessage;
import fr.milekat.infra.messaging.adapter.rabbitmq.SendRabbitMessage;
import fr.milekat.infra.messaging.adapter.redis.SendRedisMessage;
import fr.milekat.infra.messaging.exeptions.MessagingLoaderException;
import fr.milekat.infra.messaging.exeptions.MessagingSendException;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;

public class Messaging {
    private final MessagingImplementation messagingImplementation;

    public Messaging(@NotNull FileConfiguration config) throws MessagingLoaderException {
        try {
            String messagingProvider = config.getString("messaging.type");
            if (Main.DEBUG) {
                Main.getOwnLogger().info("Loading messaging type: " + messagingProvider);
            }
            if (messagingProvider.equalsIgnoreCase("none")) {
                messagingImplementation = new DefaultMessagingImplementation();
                return;
            } else if (messagingProvider.equalsIgnoreCase("rabbitmq")) {
                new ReceiveRabbitMessage().getRabbitConsumerThread().start();
                messagingImplementation = new SendRabbitMessage();
            } else if (messagingProvider.equalsIgnoreCase("redis")) {
                // TODO: 02/10/2022 Redis consumer
                messagingImplementation = new SendRedisMessage();
            } else {
                throw new MessagingLoaderException("Unsupported messaging type");
            }
            if (messagingImplementation.checkSending()) {
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

    public MessagingImplementation getMessaging() {
        return this.messagingImplementation;
    }

    public static class DefaultMessagingImplementation implements MessagingImplementation {}
}
