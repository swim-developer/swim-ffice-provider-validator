package com.github.swim_developer.validator.ffice.provider.application.usecase;

import com.github.swim_developer.validator.ffice.provider.domain.model.FficeMessage;
import jakarta.enterprise.context.ApplicationScoped;
import org.jboss.logging.Logger;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@ApplicationScoped
public class FficeMessageExtractor {

    private static final Logger LOG = Logger.getLogger(FficeMessageExtractor.class);
    private static final Pattern GUFI_PATTERN = Pattern.compile("<[^:]*:?gufi[^>]*>([^<]+)</");
    private static final Pattern DEPARTURE_PATTERN = Pattern.compile("<[^:]*:?departureAerodrome[^>]*>.*?<[^:]*:?locationIndicator>([A-Z]{4})</", Pattern.DOTALL);
    private static final Pattern DESTINATION_PATTERN = Pattern.compile("<[^:]*:?destinationAerodrome[^>]*>.*?<[^:]*:?locationIndicator>([A-Z]{4})</", Pattern.DOTALL);
    private static final Pattern AIRCRAFT_ID_PATTERN = Pattern.compile("<[^:]*:?aircraftIdentification>([^<]+)</");

    public FficeMessage extract(String xmlBody) {
        if (xmlBody == null || xmlBody.isBlank()) {
            return new FficeMessage("UNKNOWN", "UNKNOWN", null, null, null, null);
        }
        try {
            String messageType = detectMessageType(xmlBody);
            String gufi = extractFirst(GUFI_PATTERN, xmlBody, "UNKNOWN");
            String departure = extractFirst(DEPARTURE_PATTERN, xmlBody, null);
            String destination = extractFirst(DESTINATION_PATTERN, xmlBody, null);
            String aircraftId = extractFirst(AIRCRAFT_ID_PATTERN, xmlBody, null);
            return new FficeMessage(gufi, messageType, departure, destination, aircraftId, null);
        } catch (Exception e) {
            LOG.warnf("Failed to extract FF-ICE message data: %s", e.getMessage());
            return new FficeMessage("UNKNOWN", "UNKNOWN", null, null, null, null);
        }
    }

    private String detectMessageType(String xml) {
        if (xml.contains("FiledFlightPlan") || xml.contains("filedFlightPlan")) return "FILED_FLIGHT_PLAN";
        if (xml.contains("FilingStatus") || xml.contains("filingStatus")) return "FILING_STATUS";
        if (xml.contains("FlightDeparture") || xml.contains("flightDeparture")) return "FLIGHT_DEPARTURE";
        if (xml.contains("FlightArrival") || xml.contains("flightArrival")) return "FLIGHT_ARRIVAL";
        if (xml.contains("FlightCancellation") || xml.contains("flightCancellation")) return "FLIGHT_CANCELLATION";
        if (xml.contains("FlightPlanUpdate") || xml.contains("flightPlanUpdate")) return "FLIGHT_PLAN_UPDATE";
        if (xml.contains("PlanningStatus") || xml.contains("planningStatus")) return "PLANNING_STATUS";
        return "UNKNOWN";
    }

    private String extractFirst(Pattern pattern, String input, String defaultValue) {
        Matcher matcher = pattern.matcher(input);
        return matcher.find() ? matcher.group(1) : defaultValue;
    }
}
