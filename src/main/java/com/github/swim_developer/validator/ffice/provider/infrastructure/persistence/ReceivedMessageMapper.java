package com.github.swim_developer.validator.ffice.provider.infrastructure.persistence;

import com.github.swim_developer.validator.ffice.provider.domain.model.ReceivedMessage;
import com.github.swim_developer.validator.ffice.provider.infrastructure.persistence.entity.ReceivedMessageEntity;

public class ReceivedMessageMapper {

    private ReceivedMessageMapper() {}

    public static ReceivedMessageEntity toEntity(ReceivedMessage domain) {
        ReceivedMessageEntity e = new ReceivedMessageEntity();
        e.subscriptionId = domain.getSubscriptionId();
        e.queueName = domain.getQueueName();
        e.messageId = domain.getMessageId();
        e.contentType = domain.getContentType();
        e.messageType = domain.getMessageType();
        e.gufi = domain.getGufi();
        e.departureAerodrome = domain.getDepartureAerodrome();
        e.destinationAerodrome = domain.getDestinationAerodrome();
        e.aircraftIdentification = domain.getAircraftIdentification();
        e.body = domain.getBody();
        e.receivedAt = domain.getReceivedAt();
        return e;
    }

    public static ReceivedMessage toDomain(ReceivedMessageEntity e) {
        ReceivedMessage d = new ReceivedMessage();
        d.setId(e.id);
        d.setSubscriptionId(e.subscriptionId);
        d.setQueueName(e.queueName);
        d.setMessageId(e.messageId);
        d.setContentType(e.contentType);
        d.setMessageType(e.messageType);
        d.setGufi(e.gufi);
        d.setDepartureAerodrome(e.departureAerodrome);
        d.setDestinationAerodrome(e.destinationAerodrome);
        d.setAircraftIdentification(e.aircraftIdentification);
        d.setBody(e.body);
        d.setReceivedAt(e.receivedAt);
        return d;
    }
}
