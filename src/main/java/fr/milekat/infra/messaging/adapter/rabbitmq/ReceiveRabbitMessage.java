package fr.milekat.infra.messaging.adapter.rabbitmq;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import fr.milekat.infra.Main;
import fr.milekat.infra.messaging.MessageCase;
import fr.milekat.infra.messaging.MessagingImplementation;
import fr.milekat.infra.messaging.processing.MessageFromHost;
import fr.milekat.infra.messaging.processing.MessageFromLobby;
import fr.milekat.infra.messaging.processing.MessageFromProxy;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.TimeoutException;

public class ReceiveRabbitMessage {
    private final ConnectionFactory factory;

    public ReceiveRabbitMessage() {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost(Main.getFileConfig().getString("messaging.rabbit-mq.hostname"));
        connectionFactory.setPort(Main.getFileConfig().getInt("messaging.rabbit-mq.port"));
        connectionFactory.setUsername(Main.getFileConfig().getString("messaging.rabbit-mq.username"));
        connectionFactory.setPassword(Main.getFileConfig().getString("messaging.rabbit-mq.password"));
        this.factory = connectionFactory;
    }

    /**
     * Get a new RabbitMQ Consumer Thread
     */
    public Thread getRabbitConsumerThread() {
        if (Main.DEBUG) Main.getOwnLogger().info("Loading RabbitMQ consumer..");
        return new Thread(() -> {
            try {
                Connection connection = this.factory.newConnection();
                Channel channel = connection.createChannel();
                channel.exchangeDeclare(MessagingImplementation.RABBIT_EXCHANGE,
                        MessagingImplementation.RABBIT_EXCHANGE_TYPE);
                channel.queueDeclare(MessagingImplementation.RABBIT_QUEUE, false, true,
                        true, null);
                channel.queueBind(MessagingImplementation.RABBIT_QUEUE, MessagingImplementation.RABBIT_EXCHANGE,
                        MessagingImplementation.RABBIT_ROUTING_KEY);
                DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                    String strRaw = "";
                    try {
                        strRaw = new String(delivery.getBody(), StandardCharsets.UTF_8);
                        if (Main.DEBUG) {
                            Main.getOwnLogger().info(strRaw);
                        }
                        List<String> message = new Gson().fromJson(strRaw, new TypeToken<List<String>>(){}.getType());
                        if (message.size() < 2) {
                            if (Main.DEBUG) {
                                Main.getOwnLogger().warning(MessageCase.class.getName() +
                                        " not found in message: " + message);
                            }
                            return;
                        }
                        if (message.get(0).startsWith(Main.PROXY_PREFIX)) {
                            //  Message is sent from a proxy server
                            new MessageFromProxy(message);
                        } else if (message.get(0).startsWith(Main.LOBBY_PREFIX)) {
                            //  Message is sent from a lobby server
                            new MessageFromLobby(message);
                        } else if (message.get(0).startsWith(Main.HOST_PREFIX)) {
                            //  Message is sent from a host server
                            new MessageFromHost(message);
                        }
                    } catch (Exception exception) {
                        if (Main.DEBUG) {
                            Main.getOwnLogger().warning("Error while trying to consume Rabbit message !");
                            Main.getOwnLogger().warning("Message: [" + strRaw + "]");
                            exception.printStackTrace();
                        }
                    }
                };
                channel.basicConsume(MessagingImplementation.RABBIT_QUEUE, true, deliverCallback,
                        consumerTag -> {});
            } catch (IOException | TimeoutException exception) {
                exception.printStackTrace();
            }
        });
    }
}
