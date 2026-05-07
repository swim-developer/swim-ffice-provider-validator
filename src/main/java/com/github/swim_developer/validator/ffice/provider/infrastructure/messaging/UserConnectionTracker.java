package com.github.swim_developer.validator.ffice.provider.infrastructure.messaging;

import com.github.swim_developer.validator.ffice.provider.domain.port.in.ConnectionTrackerPort;
import com.github.swim_developer.validator.ffice.provider.domain.port.in.ConsoleNotificationPort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ApplicationScoped
public class UserConnectionTracker implements ConnectionTrackerPort {

    private static final Logger LOG = Logger.getLogger(UserConnectionTracker.class);
    private static final long STALE_THRESHOLD_SECONDS = 90;

    @Inject UserReceiverLifecycle receiverLifecycle;
    @Inject ConsoleNotificationPort console;

    private final Map<String, ConnectionState> connections = new ConcurrentHashMap<>();

    @Override
    public void connect(String userId, String token, String amqpHost, int amqpPort,
                        String username, String password) {
        connections.put(userId, new ConnectionState(userId, token, amqpHost, amqpPort, username, password));
        receiverLifecycle.createReceiversForUser(userId, amqpHost, amqpPort, username, password);
        console.amqpConnected(userId);
    }

    @Override
    public void disconnect(String userId) {
        if (connections.remove(userId) != null) {
            receiverLifecycle.closeAllForUser(userId);
            console.amqpDisconnected(userId);
        }
    }

    @Override
    public void createReceiver(String userId, String queueName) {
        ConnectionState s = connections.get(userId);
        if (s != null) receiverLifecycle.createReceiver(userId, queueName, s.amqpHost, s.amqpPort, s.username, s.password);
    }

    @Override
    public void closeReceiverForQueue(String userId, String queueName) {
        receiverLifecycle.closeReceiver(userId, queueName);
    }

    @Override
    public boolean isConnected(String userId) { return connections.containsKey(userId); }

    @Override
    public Map<String, String> getReceiverStatus(String userId) {
        return receiverLifecycle.getReceiverStatus(userId);
    }

    @Override
    public void heartbeat(String userId, String token) {
        ConnectionState s = connections.get(userId);
        if (s != null) { s.lastHeartbeat = Instant.now(); s.token = token; }
    }

    @Override
    public void performCleanup() {
        Instant threshold = Instant.now().minusSeconds(STALE_THRESHOLD_SECONDS);
        connections.entrySet().removeIf(entry -> {
            if (entry.getValue().lastHeartbeat.isBefore(threshold)) {
                receiverLifecycle.closeAllForUser(entry.getKey());
                LOG.infof("Cleaned up stale connection for user %s", entry.getKey());
                return true;
            }
            return false;
        });
    }

    @Override
    public boolean testQueueAccess(String userId, String queueName) {
        ConnectionState s = connections.get(userId);
        if (s == null) return false;
        return receiverLifecycle.testQueueAccess(userId, queueName, s.amqpHost, s.amqpPort, s.username, s.password);
    }

    private static class ConnectionState {
        final String userId;
        String token;
        final String amqpHost;
        final int amqpPort;
        final String username;
        final String password;
        Instant lastHeartbeat;

        ConnectionState(String userId, String token, String amqpHost, int amqpPort,
                        String username, String password) {
            this.userId = userId;
            this.token = token;
            this.amqpHost = amqpHost;
            this.amqpPort = amqpPort;
            this.username = username;
            this.password = password;
            this.lastHeartbeat = Instant.now();
        }
    }
}
