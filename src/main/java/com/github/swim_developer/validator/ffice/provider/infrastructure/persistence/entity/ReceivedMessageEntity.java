package com.github.swim_developer.validator.ffice.provider.infrastructure.persistence.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "received_messages")
public class ReceivedMessageEntity extends PanacheEntity {

    @Column(name = "subscription_id")
    public String subscriptionId;

    @Column(name = "queue_name")
    public String queueName;

    @Column(name = "message_id")
    public String messageId;

    @Column(name = "content_type")
    public String contentType;

    @Column(name = "message_type")
    public String messageType;

    @Column(length = 255)
    public String gufi;

    @Column(name = "departure_aerodrome", length = 10)
    public String departureAerodrome;

    @Column(name = "destination_aerodrome", length = 10)
    public String destinationAerodrome;

    @Column(name = "aircraft_identification", length = 20)
    public String aircraftIdentification;

    @Column(columnDefinition = "LONGTEXT")
    public String body;

    @Column(name = "received_at")
    public LocalDateTime receivedAt;
}
