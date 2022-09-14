package fr.milekat.infra.messaging.adapter.rabbitmq;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import fr.milekat.infra.Main;
import fr.milekat.infra.api.classes.ServerType;
import fr.milekat.infra.messaging.Messaging;
import fr.milekat.infra.workers.host.messaging.HostProxyReceive;
import fr.milekat.infra.workers.lobby.LobbyProxyReceive;

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
                channel.exchangeDeclare(Messaging.RABBIT_EXCHANGE, Messaging.RABBIT_EXCHANGE_TYPE);
                channel.queueDeclare(Messaging.RABBIT_QUEUE, false, true,
                        true, null);
                channel.queueBind(Messaging.RABBIT_QUEUE, Messaging.RABBIT_EXCHANGE,
                        Messaging.RABBIT_ROUTING_KEY);
                DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                    try {
                        String strRaw = new String(delivery.getBody(), StandardCharsets.UTF_8);
                        if (Main.DEBUG) {
                            Main.getOwnLogger().info(strRaw);
                        }
                        List<String> message = new Gson().fromJson(strRaw, new TypeToken<List<String>>(){}.getType());
                        if (Main.SERVER_TYPE.equals(ServerType.LOBBY) &&
                                delivery.getEnvelope().getRoutingKey().startsWith(Messaging.TARGET_TO_LOBBY_PREFIX)) {
                            //  Check if message is addressed to this lobby
                            new LobbyProxyReceive(); // TODO: 08/09/2022 LobbyProxyReceive
                        } else if (Main.SERVER_TYPE.equals(ServerType.HOST) &&
                                delivery.getEnvelope().getRoutingKey().startsWith(Messaging.TARGET_TO_HOST_PREFIX)) {
                            //  Check if message is addressed to this host
                            new HostProxyReceive(message);
                        }
                    } catch (Exception exception) {
                        if (Main.DEBUG) {
                            Main.getOwnLogger().warning("Error while trying to consume Rabbit message !");
                            exception.printStackTrace();
                        }
                    }
                };
                channel.basicConsume(Messaging.RABBIT_QUEUE, true, deliverCallback, consumerTag -> {});
            } catch (IOException | TimeoutException exception) {
                exception.printStackTrace();
            }
        });
    }
}
