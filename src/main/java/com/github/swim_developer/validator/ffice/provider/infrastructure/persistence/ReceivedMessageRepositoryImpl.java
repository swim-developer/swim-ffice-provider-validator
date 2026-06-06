package com.github.swim_developer.validator.ffice.provider.infrastructure.persistence;

import com.github.swim_developer.validator.ffice.provider.domain.model.ReceivedMessage;
import com.github.swim_developer.validator.ffice.provider.domain.port.out.ReceivedMessageRepository;
import com.github.swim_developer.validator.ffice.provider.infrastructure.persistence.entity.ReceivedMessageEntity;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class ReceivedMessageRepositoryImpl implements ReceivedMessageRepository {

    @Override
    public ReceivedMessage insert(ReceivedMessage message) {
        ReceivedMessageEntity entity = ReceivedMessageMapper.toEntity(message);
        entity.persist();
        return ReceivedMessageMapper.toDomain(entity);
    }

    @Override
    public Optional<ReceivedMessage> findMessageById(Long id) {
        return ReceivedMessageEntity.<ReceivedMessageEntity>findByIdOptional(id)
            .map(ReceivedMessageMapper::toDomain);
    }

    @Override
    public List<ReceivedMessage> findBySubscriptionId(String subscriptionId) {
        return ReceivedMessageEntity.<ReceivedMessageEntity>list("subscriptionId", subscriptionId)
            .stream().map(ReceivedMessageMapper::toDomain).toList();
    }

    @Override
    public List<ReceivedMessage> findByQueueName(String queueName) {
        return ReceivedMessageEntity.<ReceivedMessageEntity>list("queueName", queueName)
            .stream().map(ReceivedMessageMapper::toDomain).toList();
    }

    @Override
    public long countBySubscriptionId(String subscriptionId) {
        return ReceivedMessageEntity.count("subscriptionId", subscriptionId);
    }

    @Override
    public List<ReceivedMessage> findRecentMessages(int limit) {
        return ReceivedMessageEntity.<ReceivedMessageEntity>find("order by receivedAt desc")
            .page(0, limit).list()
            .stream().map(ReceivedMessageMapper::toDomain).toList();
    }
}
