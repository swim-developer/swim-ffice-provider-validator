package com.github.swim_developer.validator.ffice.provider.application.usecase;

import com.github.swim_developer.validator.ffice.provider.domain.port.in.ConsoleNotificationPort;
import jakarta.enterprise.context.ApplicationScoped;
import org.jboss.logging.Logger;

@ApplicationScoped
public class ConsoleService implements ConsoleNotificationPort {

    private static final Logger LOG = Logger.getLogger(ConsoleService.class);

    @Override
    public void info(String message) { LOG.info("[CONSOLE] " + message); }

    @Override
    public void error(String message) { LOG.error("[CONSOLE] " + message); }

    @Override
    public void amqpConnected(String userId) {
        LOG.infof("[AMQP] User %s connected to broker", userId);
    }

    @Override
    public void amqpDisconnected(String userId) {
        LOG.infof("[AMQP] User %s disconnected from broker", userId);
    }

    @Override
    public void messageReceived(String queueName, String messageType) {
        LOG.infof("[AMQP] Message received — queue=%s type=%s", queueName, messageType);
    }
}
