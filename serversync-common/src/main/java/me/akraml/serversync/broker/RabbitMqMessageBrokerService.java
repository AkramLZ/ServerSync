package me.akraml.serversync.broker;

import com.rabbitmq.client.Connection;
import me.akraml.serversync.connection.ConnectionResult;
import me.akraml.serversync.connection.auth.AuthenticatedConnection;
import me.akraml.serversync.connection.auth.ConnectionCredentials;
import me.akraml.serversync.server.ServersManager;

/**
 * A concrete implementation of the {@link MessageBrokerService} that utilizes RabbitMQ as the message broker backend.
 * This class is just like {@link RedisMessageBrokerService}, but the main difference is the software which handles
 * messages.
 *
 * TODO Implement RabbitMQ Message broker service.
 */
public class RabbitMqMessageBrokerService extends MessageBrokerService implements AuthenticatedConnection<Connection> {

    public RabbitMqMessageBrokerService(ServersManager serversManager) {
        super(serversManager);
    }

    @Override
    public void startHandler() {

    }

    @Override
    public void stop() {

    }

    @Override
    public void publish(String message) {

    }

    @Override
    public ConnectionResult connect() {
        return null;
    }

    @Override
    public Connection getConnection() {
        return null;
    }

    @Override
    public ConnectionCredentials getCredentials() {
        return null;
    }
}
