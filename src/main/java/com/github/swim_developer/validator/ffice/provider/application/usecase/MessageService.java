package com.github.swim_developer.validator.ffice.provider.application.usecase;

import com.github.swim_developer.validator.ffice.provider.domain.model.ReceivedMessage;
import com.github.swim_developer.validator.ffice.provider.domain.port.in.MessagePersistencePort;
import com.github.swim_developer.validator.ffice.provider.domain.port.in.MessagePort;
import com.github.swim_developer.validator.ffice.provider.domain.port.out.ReceivedMessageRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.List;

@ApplicationScoped
public class MessageService implements MessagePersistencePort, MessagePort {

    @Inject
    ReceivedMessageRepository repository;

    @Override
    @Transactional
    public void save(ReceivedMessage message) {
        repository.insert(message);
    }

    @Override
    public List<ReceivedMessage> findBySubscriptionId(String subscriptionId) {
        return repository.findBySubscriptionId(subscriptionId);
    }

    @Override
    public List<ReceivedMessage> findRecent(int limit) {
        return repository.findRecentMessages(limit);
    }
}
