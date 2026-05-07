package com.github.swim_developer.validator.ffice.provider.domain.model;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
public class ReceivedMessage {
    private Long id;
    private String subscriptionId;
    private String queueName;
    private String messageId;
    private String contentType;
    private String messageType;
    private String gufi;
    private String departureAerodrome;
    private String destinationAerodrome;
    private String aircraftIdentification;
    private String body;
    private LocalDateTime receivedAt;
}
