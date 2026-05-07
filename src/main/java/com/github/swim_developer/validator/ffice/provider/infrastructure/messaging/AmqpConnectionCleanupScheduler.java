package com.github.swim_developer.validator.ffice.provider.infrastructure.messaging;

import com.github.swim_developer.validator.ffice.provider.domain.port.in.ConnectionTrackerPort;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class AmqpConnectionCleanupScheduler {

    @Inject
    ConnectionTrackerPort connectionTracker;

    @Scheduled(every = "60s")
    void cleanupStaleConnections() {
        connectionTracker.performCleanup();
    }
}
