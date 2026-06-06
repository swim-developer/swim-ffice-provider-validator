package com.github.swim_developer.validator.ffice.provider.domain.port.out;

import com.github.swim_developer.validator.ffice.provider.domain.model.ReceivedMessage;
import java.util.List;
import java.util.Optional;

public interface ReceivedMessageRepository {
    ReceivedMessage insert(ReceivedMessage message);
    Optional<ReceivedMessage> findMessageById(Long id);
    List<ReceivedMessage> findBySubscriptionId(String subscriptionId);
    List<ReceivedMessage> findByQueueName(String queueName);
    long countBySubscriptionId(String subscriptionId);
    List<ReceivedMessage> findRecentMessages(int limit);
}
