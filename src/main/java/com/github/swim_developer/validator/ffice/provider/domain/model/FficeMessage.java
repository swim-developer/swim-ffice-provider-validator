package com.github.swim_developer.validator.ffice.provider.domain.model;

public record FficeMessage(
        String gufi,
        String messageType,
        String departureAerodrome,
        String destinationAerodrome,
        String aircraftIdentification,
        String filingTime
) {}
