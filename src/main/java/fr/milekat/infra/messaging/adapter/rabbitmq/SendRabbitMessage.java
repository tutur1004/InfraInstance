package fr.milekat.infra.messaging.adapter.rabbitmq;

import com.google.gson.Gson;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import fr.milekat.infra.Main;
import fr.milekat.infra.messaging.MessageCase;
import fr.milekat.infra.messaging.MessagingImplementation;
import fr.milekat.infra.messaging.exeptions.MessagingLoaderException;
import fr.milekat.infra.messaging.exeptions.MessagingSendException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

public class SendRabbitMessage implements MessagingImplementation {
    private final ConnectionFactory factory;
    private final boolean activate;

    public SendRabbitMessage() throws MessagingLoaderException {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost(Main.getConfigs().getString("messaging.rabbit-mq.hostname"));
        connectionFactory.setPort(Main.getConfigs().getInt("messaging.rabbit-mq.port"));
        connectionFactory.setUsername(Main.getConfigs().getString("messaging.rabbit-mq.username"));
        connectionFactory.setPassword(Main.getConfigs().getString("messaging.rabbit-mq.password"));
        try (Connection connection = connectionFactory.newConnection();
             Channel channel = connection.createChannel()) {
            channel.exchangeDeclare(MessagingImplementation.RABBIT_EXCHANGE,
                    MessagingImplementation.RABBIT_EXCHANGE_TYPE);
        } catch (IOException | TimeoutException exception) {
            throw new MessagingLoaderException("Error while trying to init RabbitMQ sending");
        }
        this.factory = connectionFactory;
        activate = true;
    }

    @Override
    public boolean isActivate() {
        return activate;
    }

    @Override
    public boolean checkSending() throws MessagingSendException {
        try (Connection connection = this.factory.newConnection();
             Channel ignored = connection.createChannel()) {
            return true;
        } catch (Exception exception) {
            throw new MessagingSendException(exception, "Error while trying to send message");
        }
    }

    @Override
    public void disconnect() {
        //  Nothing to do with RabbitMQ :)
    }

    /**
     * Send a message to the proxy server
     *
     * @param target  Targeted channel (RoutingKey for RabbitMQ)
     * @param mCase   Type of message
     * @param message to send
     */
    @Override
    public void sendMessage(String target, MessageCase mCase, List<String> message)
            throws MessagingSendException {
        try (Connection connection = this.factory.newConnection();
             Channel channel = connection.createChannel()) {
            List<String> list = new ArrayList<>();
            list.add(MessagingImplementation.PREFIX + MessagingImplementation.getServerIdentifier());
            list.add(mCase.name());
            list.addAll(message);
            channel.basicPublish(MessagingImplementation.RABBIT_EXCHANGE, target, null,
                    new Gson().toJson(list).getBytes(StandardCharsets.UTF_8));
        } catch (Exception exception) {
            throw new MessagingSendException(exception, "Error while trying to send message");
        }
    }
}
