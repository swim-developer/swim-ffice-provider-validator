package com.github.swim_developer.validator.ffice.provider.domain.port.in;

public interface ProviderSubscriptionPort {
    void onSubscriptionActivated(String userId, String subscriptionId, String queueName);
    void onSubscriptionPaused(String userId, String subscriptionId, String queueName);
    void onSubscriptionDeleted(String userId, String subscriptionId, String queueName);
}
