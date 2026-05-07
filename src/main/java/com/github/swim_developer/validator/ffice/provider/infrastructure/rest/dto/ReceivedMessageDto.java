package com.github.swim_developer.validator.ffice.provider.infrastructure.rest.dto;

import com.github.swim_developer.validator.ffice.provider.domain.model.ReceivedMessage;

public record ReceivedMessageDto(
    Long id, String subscriptionId, String queueName, String messageId,
    String messageType, String gufi, String departureAerodrome,
    String destinationAerodrome, String aircraftIdentification, String receivedAt
) {
    public static ReceivedMessageDto from(ReceivedMessage m) {
        return new ReceivedMessageDto(
            m.getId(), m.getSubscriptionId(), m.getQueueName(), m.getMessageId(),
            m.getMessageType(), m.getGufi(), m.getDepartureAerodrome(),
            m.getDestinationAerodrome(), m.getAircraftIdentification(),
            m.getReceivedAt() != null ? m.getReceivedAt().toString() : null
        );
    }
}
