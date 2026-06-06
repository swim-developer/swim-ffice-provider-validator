package com.github.swim_developer.validator.ffice.provider.domain.port.in;

public interface ConsoleNotificationPort {
    void info(String message);
    void error(String message);
    void amqpConnected(String userId);
    void amqpDisconnected(String userId);
    void messageReceived(String queueName, String messageType);
}
